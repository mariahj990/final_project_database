package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    
    private final DataSource dataSource;

    @Autowired
    public RatingService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Add/update a user's rating for a book and updates the book's average rating.
     * Returns true if successful
     */
    public boolean rateBook(String userId, int bookId, int rating) throws SQLException {
        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating: " + rating + ". Must be 1-5.");
            return false;
        }
        
        // Check if user already rated this book
        Integer existingRating = getUserRating(userId, bookId);
        boolean isNewRating = (existingRating == null);
        
        // Insert or update the rating
        final String newRating = """
            insert into ratings (userId, bookId, rating)
            values (?, ?, ?)
            on duplicate key update rating = ?
        """;
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(newRating)) {
            stmt.setString(1, userId);
            stmt.setInt(2, bookId);
            stmt.setInt(3, rating);
            stmt.setInt(4, rating);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update the book's average rating
                updateBookAverageRating(bookId, rating, existingRating, isNewRating);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Update the book's average rating and ratings count.
     * Uses weighted average calculation.
     */
    private void updateBookAverageRating(int bookId, int newRating, Integer oldRating, boolean isNewRating) throws SQLException {
        // Get current average and count
        final String getBookStats = "select average_rating, ratings_count from book where bookId = ?";
        double currentAvg = 0;
        int currentCount = 0;
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getBookStats)) {
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    currentAvg = rs.getDouble("average_rating");
                    currentCount = rs.getInt("ratings_count");
                }
            }
        }
        
        // Calculate new average
        double newAvg;
        int newCount;
        
        if (isNewRating) {
            //adding a new rating
            newAvg = (currentAvg * currentCount + newRating) / (currentCount + 1);
            newCount = currentCount + 1;
        } else {
            //updating existing rating
            newAvg = (currentAvg * currentCount - oldRating + newRating) / currentCount;
            newCount = currentCount;
        }
        
        // Update the book table
        final String updateBook = "update book set average_rating = ?, ratings_count = ? where bookId = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateBook)) {
            stmt.setDouble(1, newAvg);
            stmt.setInt(2, newCount);
            stmt.setInt(3, bookId);
            stmt.executeUpdate();
        }
        
        System.out.println("Updated book " + bookId + " rating: " + newAvg + " (count: " + newCount + ")");
    }

    /**
     * Get a user's rating for a book if it exists.
     * Returns null if no rating found.
     */
    public Integer getUserRating(String userId, int bookId) throws SQLException {
        final String query = "select rating from ratings where userId = ? and bookId = ?";
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setInt(2, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        }
        
        return null;
    }
}