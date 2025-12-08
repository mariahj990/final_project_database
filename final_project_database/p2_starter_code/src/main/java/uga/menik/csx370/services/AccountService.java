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
        System.out.println("getCurrentUser: " + userId);
        
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
        System.out.println("User not found.");
        return null;

    }

    public int getCurrentUserNumWishlist() {
        int numWishlist = 0;
        String userId = userService.getLoggedInUser().getUserId();
        System.out.println("getCurrentUser: " + userId);

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

    public List<Book> getCurrentUserWishlist() {
        List<Book> wishlistBooks = new ArrayList<>();
        String userId = userService.getLoggedInUser().getUserId();

        final String getWishlistBooks = "SELECT b.bookId, b.title, b.authors, b.isbn13, " +
                                                "b.description, b.genres, b.average_rating, " +
                                                "b.original_publication_year, b.ratings_count," +
                                                "b.image_url, b.total_copies, b.page_count " +
                                            "FROM history AS his " +
                                            "JOIN book AS b ON b.bookId = his.bookId " +
                                            "WHERE his.userId = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getWishlistBooks)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(rs.getInt("b.bookId"), rs.getString("b.title"),
                                        rs.getString("b.authors"), rs.getString("b.isbn13"),
                                        rs.getString("b.description"), rs.getString("b.genres"),
                                        rs.getDouble("b.average_rating"), rs.getInt("b.original_publication_year"),
                                        rs.getInt("b.ratings_count"), rs.getString("b.image_url"), 
                                        rs.getInt("b.total_copies"), rs.getInt("b.page_count"));
                    wishlistBooks.add(book);
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return wishlistBooks;
    }

    public int getCurrentUserNumCheckout() {
        int numCheckOut = 0;
        String userId = userService.getLoggedInUser().getUserId();
        System.out.println("getCurrentUser: " + userId);

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

}
