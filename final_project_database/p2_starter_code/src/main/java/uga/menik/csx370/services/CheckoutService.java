package uga.menik.csx370.services;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.CheckedOutBook;
import uga.menik.csx370.models.User;

@Service
public class CheckoutService {
    private final DataSource dataSource;
    private final BookService bookService;
    private final GenreService genreService;
    private final ForYouPageService forYouPageService;
    
    @Autowired
    public CheckoutService(DataSource datasource, BookService bookService, GenreService genreService, ForYouPageService forYouPageService) {
        this.dataSource = datasource;
        this.bookService = bookService;
        this.genreService = genreService;
        this.forYouPageService = forYouPageService;
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

    public List<CheckedOutBook> getUsersCheckedOutBooks(User user) throws SQLException {

        List<CheckedOutBook> books = new ArrayList<>();

        // User this_user = userService.getLoggedInUser();  //get the logged in user
        String userId = user.getUserId(); //get the id of the logged in user
        
        final String getBookIds = "SELECT bookId, checkout_date from curr_checkout where userId = ?"; //get all book Ids
        try(Connection conn = dataSource.getConnection();
        PreparedStatement postStmt = conn.prepareStatement(getBookIds)) {//passes sql query
            //fill in the ?'s
            postStmt.setString(1, userId); //for logged in user        
            try(ResultSet rs = postStmt.executeQuery()) {
                while (rs.next()) {
                    int bookId = rs.getInt("bookId"); //get bookId
                    Book this_book = bookService.getBook(bookId); //get book object from book service 
                    // get regular book attributes
                    // Need int bookId, String title, String authors, double average_rating, Date due_date 
                    // make into a checked_out book object
                    Date checkout_date = rs.getDate("checkout_date");
                    LocalDate dueLocalDate = checkout_date.toLocalDate().plusWeeks(2); // add 2 weeks to checkout date
                    Date due_date = Date.valueOf(dueLocalDate);

                    double ratingFormatted = this_book.getAverage_rating();
                    ratingFormatted = Math.floor((ratingFormatted*1000))/1000;

                    CheckedOutBook this_checkout = new CheckedOutBook(bookId,this_book.getTitle(), this_book.getAuthors(), ratingFormatted, checkout_date, due_date);
                    books.add(this_checkout); //call helper method
                }
            } //try
        } //try
        return books;
    } //getUsersCheckedOutBooks


    /**
     * This function should check out a book for a user. 
     * Needs to check if the book is available first.
     * Returns true if post creation was successful. 
     */
    public boolean checkOutBook(User user, int bookId) throws SQLException {
        System.out.println("In checkOutBook function.");
        //create SQL query to insert an entry into curr_checkouts table
        // User this_user = userService.getLoggedInUser();

        // check if book is available (in bookService) 
        boolean isAvailable = bookService.getIfBookAvailable(bookId);

        // check if user already has book checked out right now
        boolean alreadyHaveBook = isCheckedOutbyUserNow(user, bookId);

        if (!isAvailable || alreadyHaveBook) {
            System.out.println("Book is not available or user has already checked it out.");
            return false; // book is not available for checkout, or user already has it checked out.
            // constraint: user can't check out book more than once at a time.
        } // else continue. 
        System.out.println("Book is available for checkout. Checking out now...");

        // need to update recommendations in for you page. 
        forYouPageService.updateRecs(user, bookId);

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
    // needs to: increment user's pages read????, books read, update history table's has_read to T 
    // remove row from curr_checkout table
    // would update user-level genre stats counts table as well once added??
    public boolean returnBook(User user, int bookId) throws SQLException {
        System.out.println("In returnBook function.");
        
        // Check if user actually has this book checked out
        if (!isCheckedOutbyUserNow(user, bookId)) {
            System.out.println("User does not have this book checked out.");
            return false;
        }
        
        System.out.println("Book is checked out. Returning now...");
        
        // Delete from curr_checkout table
        final String deleteCheckout = "delete from curr_checkout where userId = ? and bookId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement deleteStmt = conn.prepareStatement(deleteCheckout)) {
            deleteStmt.setString(1, user.getUserId());
            deleteStmt.setInt(2, bookId);
            int rowsDeleted = deleteStmt.executeUpdate();
            
            if (rowsDeleted == 0) {
                return false;
            }
        }
        
        // Update history table: mark book as read
        final String updateHistory = "insert into history (userId, bookId, has_read) values (?, ?, true) on duplicate key update has_read = true";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement historyStmt = conn.prepareStatement(updateHistory)) {
            historyStmt.setString(1, user.getUserId());
            historyStmt.setInt(2, bookId);
            historyStmt.executeUpdate();
        }
        
        // Update user's reading stats: increment books read and pages read
        Book book = bookService.getBook(bookId);
        int pageCount = book.getPage_count();
        String genresString = book.getGenres();

        final String updateUser = "update user set num_books_read = num_books_read + 1, num_pages_read = num_pages_read + ? where userId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement userStmt = conn.prepareStatement(updateUser)) {
            userStmt.setInt(1, pageCount);
            userStmt.setString(2, user.getUserId());
            userStmt.executeUpdate();
        }
        /*
        // Update genre counts
        List<String> genreBuckets = genreService.parseAndMapGenres(genresString);
        for (String bucketName : genreBuckets) {
            try {
                int genreId = genreService.getGenre(bucketName);
                genreService.incrementUserGenreCount(user.getUserId(), genreId);
            } catch (SQLException e) {
                System.out.println("Error updating genre count");
            }
        }
        */
        System.out.println("Successfully returned book.");
        return true;
    }

    
}
