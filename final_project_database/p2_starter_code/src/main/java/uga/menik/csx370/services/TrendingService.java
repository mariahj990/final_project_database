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

import uga.menik.csx370.models.Simple_Book;

@Service
public class TrendingService {

    private final DataSource dataSource;
    
    @Autowired
    public TrendingService(DataSource datasource) {
        this.dataSource = datasource;
    } //BookmarksService

    public List<Simple_Book> getAllBooks() {
        List<Simple_Book> allBooks = new ArrayList<>();
    
        final String getAllBooks = "SELECT * FROM book LIMIT 10;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAllBooks)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                        Simple_Book book = new Simple_Book(rs.getInt("bookId"), rs.getString("title"),
                                            rs.getString("authors"), rs.getDouble("average_rating"));
                        System.out.println("Added " + rs.getString("title"));
                        allBooks.add(book);
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            return allBooks;
    }
}
