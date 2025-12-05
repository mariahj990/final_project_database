package uga.menik.csx370.services;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uga.menik.csx370.services.UserService;
import uga.menik.csx370.services.BookService;
import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.User;

@Service
public class CheckoutService {
    private final DataSource dataSource;
    private final BookService bookService;
    
    @Autowired
    public CheckoutService(DataSource datasource, BookService bookService) {
        this.dataSource = datasource;
        this.bookService = bookService;
    } //CheckoutService

    // do we need an injected UserService here? to get current user.

    public boolean isCheckedOutbyUserNow (User user, int bookId) throws SQLException {
        int count_thisBookandUser = 0;
        final String querySql = "select count(*) as count_thisBookandUser from curr_checkout where userId = ? and bookId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement queryStmt = conn.prepareStatement(querySql)) {
            queryStmt.setString(1, user.getUserId());
            queryStmt.setInt(2, bookId);
            try (ResultSet rs = queryStmt.executeQuery()) {
                if (rs.next()) {
                        count_thisBookandUser = rs.getInt("count_thisBookandUser"); // read and store the value
                    }
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        if (count_thisBookandUser > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function should check out a book for a user. 
     * Needs to check if the book is available first.
     * Returns true if post creation was successful. 
     */
    public boolean checkOutBook(User user, int bookId) throws SQLException {
        //create SQL query to insert an entry into curr_checkouts table
        // User this_user = userService.getLoggedInUser();

        // check if book is available (in bookService) 
        boolean isAvailable = bookService.isBookAvailable(bookId);

        // check if user already has book checked out right now
        boolean alreadyHaveBook = isCheckedOutbyUserNow(user, bookId);

        if (!isAvailable || alreadyHaveBook) {
            return false; // book is not available for checkout, or user already has it checked out.
            // constraint: user can't check out book more than once at a time.
        } // else continue. 

        // add row to curr_checkout table. We don't consider the book "read" yet until it's returned.
        final String checkout = "insert into curr_checkout (userId, bookId, checkout_date) values (?, ?, CURDATE())";
        try (Connection conn = dataSource.getConnection(); //establish connection with database
            PreparedStatement checkoutStmt = conn.prepareStatement(checkout)) { //passes sql queary
            checkoutStmt.setString(1, user.getUserId());
            checkoutStmt.setInt(2, bookId);
            int rowsAffected = checkoutStmt.executeUpdate();
            return rowsAffected > 0;
        } //try

    } //checkOutBook

    // return book function
    // needs to: increment user's pages read, books read, update history table's has_read to T 
    // remove row from curr_checkout table
    // would update user-level genre stats counts table as well once added.


    
}
