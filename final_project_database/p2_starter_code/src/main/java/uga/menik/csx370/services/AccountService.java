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

/*
 * This service contains Account related functions. 
 */
@Service
public class AccountService {
    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public AccountService(DataSource datasource, UserService userService) {
        this.dataSource = datasource;
        this.userService = userService;
    } //BookmarksService

    public User getCurrentUser() {
        String userId = userService.getLoggedInUser().getUserId();        
        // SQL query to get all users
        final String getUserSql = "SELECT userId, firstName, lastName FROM user WHERE userId = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getUserSql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    // Initialize and retain the logged in user.
                    User loggedInUser = new User(userId, firstName, lastName);
                    return loggedInUser;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;

    }

    public int getCurrentUserNumWishlist() {
        int numWishlist = 0;
        String userId = userService.getLoggedInUser().getUserId();
        // SQL query to get all users
        final String getNumWishlist = "SELECT COUNT(*) FROM history WHERE userId = ? AND has_wishlisted = 1;";
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getNumWishlist)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    numWishlist = rs.getInt(1);
                    return numWishlist;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return numWishlist; 
    }

    public int getCurrentUserNumCheckout() {
        int numCheckOut = 0;
        String userId = userService.getLoggedInUser().getUserId();
        // SQL query to get all users
        final String getNumWishlist = "SELECT COUNT(*) AS NumCheckedOut FROM curr_checkout GROUP BY userId HAVING userId = ?";
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getNumWishlist)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    numCheckOut = rs.getInt(1);
                    return numCheckOut;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return numCheckOut; 
    }

    public int getCurrentUserNumBooksRead() {
        int numBooksRead = 0;
        String userId = userService.getLoggedInUser().getUserId();
        // SQL query to get all users
        final String getNumBooksRead = "SELECT COUNT(*) FROM history WHERE userId = ? AND has_read = 1;";
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getNumBooksRead)) {
            stmt.setString(1, userId);
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

        public int getCurrentUserNumPagesRead() {
            int numPagesRead = 0;
            String userId = userService.getLoggedInUser().getUserId();
            // SQL query to get all users
            final String getNumPagesRead = "SELECT page_count FROM history JOIN book ON history.bookId = book.bookId WHERE userId = ? AND has_read = 1;";
            
            try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(getNumPagesRead)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        numPagesRead = numPagesRead + rs.getInt("page_count");
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            return numPagesRead; 
        }

        public List<Simple_Book> getUserHistory() {
            List<Simple_Book> userHistory = new ArrayList<>();
            String userId = userService.getLoggedInUser().getUserId();

            final String getUserHistory = "SELECT DISTINCT b.bookId, b.title, b.authors, b.average_rating, b.image_url " +
                                                "FROM history AS his " +
                                                "JOIN book AS b ON b.bookId = his.bookId " +
                                                "WHERE his.userId = ?;";
            try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(getUserHistory)) {
                stmt.setString(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double ratingFormatted = rs.getDouble("average_rating");
                        ratingFormatted = Math.floor((ratingFormatted*1000))/1000;

                        Simple_Book book = new Simple_Book(rs.getInt("b.bookId"), rs.getString("b.title"),
                                            rs.getString("b.authors"), ratingFormatted, 
                                            rs.getString("b.image_url"));
                        userHistory.add(book);
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            return userHistory;
        }

        public String getTopGenre() {
            String topGenre = "None yet - Read More!";
            String userId = userService.getLoggedInUser().getUserId();

            final String getUserHistory = "SELECT genreCategoryName, max(numBooks) As num from user_genre_count WHERE userId = ? GROUP BY genreCategoryName order by max(numBooks) desc limit 1";
            try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(getUserHistory)) {
                stmt.setString(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        topGenre = rs.getString("genreCategoryName");
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            return topGenre;
        }

}
