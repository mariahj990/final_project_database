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
                        Book book = new Book(rs.getInt("bookId"), 
                                        rs.getString("title"), 
                                        rs.getString("authors"), 
                                        rs.getString("isbn13"), 
                                        rs.getString("description"), 
                                        rs.getString("genres"),
                                        rs.getDouble("average_rating"), 
                                        rs.getInt("original_publication_year"), 
                                        rs.getInt("ratings_count"),
                                        rs.getString("image_url"), 
                                        rs.getInt("total_copies"));
                                            
                        System.out.println("Opening book: " + rs.getString("title"));
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
            PreparedStatement stmt = conn.prepareStatement(checkAvailability)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                        numCheckedOut = rs.getInt("numCheckedOut"); // read and store the value
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
	System.out.println("Number of copies of book in library: " + totalCopies);
	System.out.println("Number of copies checked out currently: " + numCheckedOut);
	int numAvailable = totalCopies - numCheckedOut;
	System.out.println("Number of copies available at library: " + numAvailable);
	
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
                Book book = new Book(
                    rs.getInt("bookId"),
                    rs.getString("title"),
                    rs.getString("authors"),
                    rs.getString("isbn13"),
                    rs.getString("description"),
                    rs.getString("genres"),
                    rs.getDouble("average_rating"),
                    rs.getInt("original_publication_year"),
                    rs.getInt("ratings_count"),
                    rs.getString("image_url"),
                    rs.getInt("total_copies")
                );
                books.add(book);
            }
        }
    } catch (SQLException e) {
        System.err.println("❌ Error searching books: " + e.getMessage());
    }

    return books;
}



    
}
