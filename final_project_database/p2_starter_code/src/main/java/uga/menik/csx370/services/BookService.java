package uga.menik.csx370.services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    
}
