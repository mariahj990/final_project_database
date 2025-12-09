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
public class ForYouPageService {

    private final DataSource dataSource;
    private final UserService userService;
    
    @Autowired
    public ForYouPageService(DataSource datasource, UserService userService) {
        this.dataSource = datasource;
        this.userService = userService;
    } //TrendingService

    public boolean isNewBook(int bookId) throws SQLException {  
        User user = userService.getLoggedInUser();
        // call this method after book is checked out or added to wishlist
        // determines if user_genre_count needs to be updated

        // first seeing if in history table for that user yet.
        int found_in_history = 0;
        int found_in_current_checkout = 0;
        final String querySql = "select exists(select 1 from history where userId = ? and bookId = ? ) as found_in_history;";
        final String querySql2 = "select exists(select 1 from curr_checkout where userId = ? and bookId = ? ) as found_in_current_checkout;";
        try (Connection conn = dataSource.getConnection()){
           try(PreparedStatement queryStmt = conn.prepareStatement(querySql)) {
            queryStmt.setString(1, user.getUserId());
            queryStmt.setInt(2, bookId);
            try (ResultSet rs = queryStmt.executeQuery()) {
                if (rs.next()) {
                        found_in_history = rs.getInt("found_in_history"); // read and store the bool value
                    }
                }
            }

           try(PreparedStatement queryStmt = conn.prepareStatement(querySql2)) {
            queryStmt.setString(1, user.getUserId());
            queryStmt.setInt(2, bookId);
            try (ResultSet rs = queryStmt.executeQuery()) {
                if (rs.next()) {
                        found_in_current_checkout = rs.getInt("found_in_current_checkout"); // read and store the bool value
                    }
                }
            }            
        } catch (SQLException e) {
                System.out.println(e);
            }
        boolean inHistoryBool = found_in_history > 0;
        boolean inCCheckoutBool = found_in_current_checkout > 0;

        return !(inHistoryBool || inCCheckoutBool);

    }

    public List<Simple_Book> getCandidateBooks(){
        User user = userService.getLoggedInUser();
        String userId = user.getUserId();

        List<Simple_Book> books = new ArrayList<>();

        // Filter out books that they have checked out or in wishlist already
        // Select books that match their preferred genres from user_genre_count
        // Books with more genres in common with user's preferred genres should rank higher
        // intersection between genres is important
        String findCandidates = """
        SELECT b.bookId, b.title, b.authors, b.average_rating, b.image_url, SUM(ugc.numBooks) AS score
        FROM book b
        JOIN book_to_genre btg ON b.bookId = btg.bookId
        JOIN genre_category gc ON btg.genreName = gc.genreName
        LEFT JOIN user_genre_count ugc 
               ON ugc.genreCategoryName = gc.genreCategoryName
              AND ugc.userId = ?
        WHERE b.bookId NOT IN (
            SELECT bookId FROM history WHERE userId = ?
            UNION
            SELECT bookId FROM curr_checkout WHERE userId = ?
        )
        GROUP BY b.bookId, b.title, b.authors, b.average_rating, b.image_url
        ORDER BY score DESC
        LIMIT 20
        """; 
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(findCandidates)) {
            // Bind the same wildcard term for title, authors, and genres
            stmt.setString(1, userId);
            stmt.setString(2, userId);
            stmt.setString(3, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double ratingFormatted = rs.getDouble("average_rating");
                    ratingFormatted = Math.floor((ratingFormatted*1000))/1000;

                    Simple_Book book = new Simple_Book(
                        rs.getInt("bookId"),
                        rs.getString("title"),
                        rs.getString("authors"),
                        ratingFormatted,
                        rs.getString("image_url")
                    );
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return books;
    }//searchBooks

    public String getTopMatchingGenreForBook(int bookId) {
        User user = userService.getLoggedInUser();
        String userId = user.getUserId();
        // Extract the most important genre category (to the user) from the book 
        // help explain the scoring algorithm
        // using the user_genre_count table max genre for that user
        String topGenre = null;

        final String findTopGenre = "SELECT gc.genreCategoryName " +
        "FROM book_to_genre btg " +
        "JOIN genre_category gc ON btg.genreName = gc.genreName " +
        "LEFT JOIN user_genre_count ugc " +
                "ON ugc.genreCategoryName = gc.genreCategoryName " +
                  "AND ugc.userId = ? " +
        "WHERE btg.bookId = ? " +
        "ORDER BY ugc.numBooks DESC " +
        "LIMIT 1;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(findTopGenre)) {
            stmt.setString(1, userId);
            stmt.setInt(2, bookId); // need bookId to find top genre for that book
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    topGenre = rs.getString("genreCategoryName");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding most important genre: " + e.getMessage());
        }
        return topGenre;

    }

    public List<String> findGenres(int bookId) {
        List<String> genres = new ArrayList<>();
        // return list of genre categories for a given book
               // if they already had the genre in their history, increment numBooks by 1
        // else insert new row with numBooks = 1
        final String findGenresForBook = """
            SELECT gc.genreCategoryName
            FROM book_to_genre as btg
            JOIN genre_category as gc
              ON btg.genreName = gc.genreName
            WHERE bookId = ?;""";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(findGenresForBook)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String genreCategory = rs.getString("genreCategoryName");

                    genres.add(genreCategory);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching for genre categories to recommend: " + e.getMessage());
        }
        return genres;
        
    }


    public void updateRecs(User user, int bookId) { // call after checkout or wishlist add.
        // TODO: need to call after wishlist add !! 
        // decides if the book is new for the user (check)
        try{
            boolean needsUpdate = isNewBook(bookId);
            if (!needsUpdate) {
                return; // no update needed
            }

            // if new, adds the book's genres to user_genre_count

            // get genres for the book - most books have multiple
            List<String> genres = findGenres(bookId);

            // for each genre, add to user_genre_count
            for (String genre : genres){ 
                System.out.println("Adding genre " + genre + " to recommendations for user " + user.getUserId());
                addBookGenretoRecs(user, bookId, genre);
            }
                
            System.out.println("Recommendations successfully updated for user.");

        } catch (SQLException e){
            System.out.println("SQL Error checking if book is new for user: " + e.getMessage());
            return;
        }


    }

    public void addBookGenretoRecs(User user, int bookId, String genreCategory) {
 
        // for all genre catgories that map to book
        String userId = user.getUserId();

        final String updateSql = """
            INSERT INTO user_genre_count (userId, genreCategoryName, numBooks)
            VALUES (?, ?, 1)
            ON DUPLICATE KEY UPDATE numBooks = numBooks + 1;
            """;
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, userId);
            stmt.setString(2, genreCategory);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user_genre_count for recommendations: " + e.getMessage());
        }


    }


}
