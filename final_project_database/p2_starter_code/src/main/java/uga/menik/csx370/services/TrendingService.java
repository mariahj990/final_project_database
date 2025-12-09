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
import uga.menik.csx370.models.User;


@Service
public class TrendingService {

    private final DataSource dataSource;
    private final UserService userService;
    
    @Autowired
    public TrendingService(DataSource datasource, UserService userService) {
        this.dataSource = datasource;
        this.userService = userService;
    } //TrendingService

    public List<Simple_Book> getTop10Books() {
        List<Simple_Book> top10books = new ArrayList<>();

        final String getAllBooks = "SELECT * FROM book ORDER BY average_rating LIMIT 10;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAllBooks)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double ratingFormatted = rs.getDouble("average_rating");
                    ratingFormatted = Math.floor((ratingFormatted*1000))/1000;

                    Simple_Book book = new Simple_Book(rs.getInt("bookId"), rs.getString("title"),
                                        rs.getString("authors"), ratingFormatted, rs.getString("image_url"));
                    top10books.add(book);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return top10books;
    }

        /**
     * This function queries and returns all users.
     */
    public List<User> getTop10Users() {
        List<User> users = new ArrayList<>();
        // SQL query to get all users
        final String getTopUsersSql = "SELECT u.userId, u.firstName, u.lastName, COUNT(*) AS numRead " + 
                                        "FROM user AS u " + 
                                        "JOIN history AS h ON u.userId = h.userId " + 
                                        "WHERE h.has_read = 1 " + 
                                        "GROUP BY u.userId, u.firstName, u.lastName " + 
                                        "ORDER BY numRead DESC LIMIT 10;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getTopUsersSql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getString("userId"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return users;
    }

    public User getUserById(int userId) {
                final String getUserSql = "SELECT userId, firstName, lastName FROM user where userId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getUserSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("userId"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    );
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public int getUserNumBooksRead(int userId) {
        int numBooksRead = 0;
        // SQL query to get all users
        final String getNumBooksRead = "SELECT COUNT(*) FROM history WHERE userId = ? AND has_read = 1;";
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getNumBooksRead)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    numBooksRead = rs.getInt(1);
                    return numBooksRead;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return numBooksRead; 
    }

    public int getNumTop10Users() {
        int numTop10Users = 0;
        // SQL query to get all users
        final String getNumReadTot = "SELECT COUNT(DISTINCT u.userId) AS numReadTot "
                                        + "FROM user AS u "
                                        + "JOIN history AS h ON u.userId = h.userId "
                                        + "WHERE h.has_read = 1;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getNumReadTot)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    numTop10Users = rs.getInt("numReadTot");
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        if(numTop10Users < 10) {
            return numTop10Users;
        } else {
            return 10;
        }
    }


}
