package uga.menik.csx370.services;

//import java.awt.print.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import java.util.User;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uga.menik.csx370.models.User;
import uga.menik.csx370.models.Simple_Book;

@Service
public class WishlistService {
    private final DataSource dataSource;
    private final UserService userService;
    private final ForYouPageService forYouPageService;

    @Autowired
    public WishlistService(DataSource dataSource, UserService userService, ForYouPageService forYouPageService) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.forYouPageService = forYouPageService;
    }

    /**
     * This function queries and returns the books on the wishlist of the logged in user. 
     */
    public List<Simple_Book> getAllWhishlist() {
        List<Simple_Book> books = new ArrayList<>();
        String userId = userService.getLoggedInUser().getUserId(); // the logged in user 

        // SQL query to the books on the wishlist of the logged in user from history table
       // final String getAllUsersSql = "SELECT u.userId, u.firstName, u.lastName FROM user u";

        final String getAllWishlistBooks = "SELECT b.bookId, b.title, b.authors, b.average_rating, b.image_url FROM book b "
                + "JOIN history h ON b.bookId = h.bookId "
                + "WHERE h.userId = ? and h.has_wishlisted = true";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAllWishlistBooks)) {
                stmt.setString(1, userId); // getting the logged in user's wishlisted books 
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Simple_Book book = new Simple_Book(rs.getInt("bookId"), rs.getString("title"),
                    rs.getString("authors"), rs.getDouble("average_rating"), rs.getString("image_url"));
                    books.add(book);
                } // while 
            } // try 
        } catch (SQLException e) {
            System.out.println(e);
        }
        return books;
    } // getAllWhishlist

    public boolean addWishlist(String loggedInUserId, int bookId) {
        User user = userService.getLoggedInUser();
        forYouPageService.updateRecs(user, bookId); // updates the user-specific genre history.

        // sql statement that adds the book_id to the history table if not already in the table and sets has_wishlisted to true,  

        final String addWishlistSql = "INSERT into history (bookId, userId, has_wishlisted) VALUES (?, ?, TRUE) "
                + "ON DUPLICATE KEY UPDATE has_wishlisted = TRUE";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(addWishlistSql)) { 
                    stmt.setString(2, loggedInUserId); // the logged in user set
                    stmt.setInt(1, bookId); // the book_id to be added to the wishlist in the history table set 
                    int rows = stmt.executeUpdate(); // statement update 
                    return rows > 0; // return true if the database was updated 
                } catch (SQLException e) {
                    System.out.println(e);
                    return false; // return false otherwise 
        } // catch
    } // addWishlist

    public boolean removeWishlist(String loggedInUserId, int bookId) {
        
        // sql statment that removes the book_id from th
        final String removeWishlistSql = "UPDATE history SET has_wishlisted = FALSE WHERE userId = ? AND bookId = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(removeWishlistSql)) {
                stmt.setString(1, loggedInUserId);
                stmt.setInt(2, bookId);
                int rows = stmt.executeUpdate(); 
                return rows > 0; // return true if the database was updated
            } catch (SQLException e) {
                System.out.println("Error from trying to remove a book from wishlist: " + e);
                return false; // return false if exceitpion was thrown 
            } // catch 
    } // removeWishlist

    
    public boolean isWishlisted (String loggedInUser, int bookId) {
        final String isWishlistedSql = "SELECT has_wishlisted FROM history WHERE userId = ? AND bookId = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(isWishlistedSql)) {
                stmt.setString(1, loggedInUser);
                stmt.setInt(2, bookId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBoolean("has_wishlisted");
                    } else {
                        return false; // no entry found
                    }
                } // try 
            } catch (SQLException e) {
                System.out.println("Error from trying to check if book is wishlisted: " + e);
                return false; // return false if exception was thrown 
            } // catch 
    }
}
