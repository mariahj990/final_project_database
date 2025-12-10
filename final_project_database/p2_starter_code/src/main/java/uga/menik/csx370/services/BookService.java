/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.Simple_Book;

@Service
public class BookService {
    private final DataSource dataSource;
    
    @Autowired
    public BookService(DataSource datasource) {
        this.dataSource = datasource;
    } //BookService

    public Book getBook(int bookID) {
        final String getAllBooks = "SELECT * FROM book where bookId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAllBooks)) {
            stmt.setInt(1, bookID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                        double ratingFormatted = rs.getDouble("average_rating");
                        ratingFormatted = Math.floor((ratingFormatted*1000))/1000;
                        Book book = new Book(rs.getInt("bookId"), 
                                        rs.getString("title"), 
                                        rs.getString("authors"), 
                                        rs.getString("isbn13"), 
                                        rs.getString("description"), 
                                        rs.getString("genres"),
                                        ratingFormatted, 
                                        rs.getInt("original_publication_year"), 
                                        rs.getInt("ratings_count"),
                                        rs.getString("image_url"), 
                                        rs.getInt("total_copies"),
                                        rs.getInt("page_count"));                                            
                        return book;
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            return null;
    } //getBook

    public boolean getIfBookAvailable(int bookId) {
        final String checkAvailability = "SELECT total_copies from book where bookId = ?";
        int totalCopies = 0;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(checkAvailability)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                        totalCopies = rs.getInt("total_copies"); // read and store the value
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        final String getNumCheckedOut = "SELECT COUNT(distinct userId) as numCheckedOut from curr_checkout where bookId = ?";
        int numCheckedOut = 0;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getNumCheckedOut)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                        numCheckedOut = rs.getInt("numCheckedOut"); // read and store the value
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        return totalCopies > numCheckedOut; // returns true if there are more copies than checked out.
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        // Return early if keyword is null or blank
        if (keyword == null || keyword.trim().isEmpty()) {
            return books;
        }
        String sql = """
            SELECT * FROM book 
            WHERE LOWER(title) LIKE ? 
            OR LOWER(authors) LIKE ? 
            OR LOWER(genres) LIKE ?
            """;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + keyword.trim().toLowerCase() + "%";
            // Bind the same wildcard term for title, authors, and genres
            for (int i = 1; i <= 3; i++) {
                stmt.setString(i, searchTerm);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double ratingFormatted = rs.getDouble("average_rating");
                    ratingFormatted = Math.floor((ratingFormatted*1000))/1000;
                    
                    String formattedGenres = rs.getString("genres");
                    formattedGenres = formattedGenres.replace("[", "");
                    formattedGenres = formattedGenres.replace("]", "");
                    formattedGenres = formattedGenres.replace("'", "");

                    Book book = new Book(
                        rs.getInt("bookId"),
                        rs.getString("title"),
                        rs.getString("authors"),
                        rs.getString("isbn13"),
                        rs.getString("description"),
                        formattedGenres,
                        ratingFormatted,
                        rs.getInt("original_publication_year"),
                        rs.getInt("ratings_count"),
                        rs.getString("image_url"),
                        rs.getInt("total_copies"),
                        rs.getInt("page_count")
                    );
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return books;
    }//searchBooks

    public List<Simple_Book> youMayAlsoLike(int bookId){
        List<Simple_Book> booksPeopleAlsoLiked = new ArrayList<>();
        // Subquery: find users who have history with that given book
        // Outer query: find other books those same users have history with
        String sql = """
            SELECT distinct h.bookId 
            from history h 
            where h.userId in (
                select distinct h2.userId 
                from history h2 
                where h2.bookId = ?
            ) and h.bookId != ?
            LIMIT 10
            """; 
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int recommendedBookId = rs.getInt("bookId");
		            Book ext_book = getBook(recommendedBookId);
                    Simple_Book book = new Simple_Book(recommendedBookId, ext_book.getTitle(), ext_book.getAuthors(), ext_book.getAverage_rating(), ext_book.getImage_url());
                    if (booksPeopleAlsoLiked.contains(book) == false) {
                        booksPeopleAlsoLiked.add(book); // add if not already in list
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recommendations: " + e.getMessage());
        }
        return booksPeopleAlsoLiked;
    }   
}
