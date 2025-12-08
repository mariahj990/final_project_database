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
        User user = userService.getCurrentUser();
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
        return !(found_in_history || found_in_current_checkout);

    }

    public List<SimpleBook> getCandidateBooks(){
        User user = userService.getCurrentUser();
        String userId = user.getUserId();

        List<Book> books = new ArrayList<>();

        String findCandidates = """
        SELECT b.bookId, b.title, b.authors, b.average_rating, SUM(ugc.numBooks) AS score
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
        GROUP BY b.bookId, b.title, b.authors, b.average_rating
        ORDER BY score DESC
        LIMIT 10
        """; 
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(findCandidates)) {
            // Bind the same wildcard term for title, authors, and genres
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SimpleBook book = new SimpleBook(
                        rs.getInt("bookId"),
                        rs.getString("title"),
                        rs.getString("authors"),
                        rs.getDouble("average_rating")
                    );
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return books;
    }//searchBooks

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
            return genres;

        } catch (SQLException e) {
            System.err.println("Error searching for genre categories to recommend: " + e.getMessage());
        }
        
    }


    public void updateRecs(User user, int bookId) { // call after checkout or wishlist add.
        // TODO: need to call after wishlist add !! 
        // decides if the book is new for the user (check)

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
    }

    public void addBookGenretoRecs(User user, int bookId, String genreCategory) {
 
        // for all genre catgories that map to book
        String userId = user.getUserId();

        final String updateSql = """
            INSERT INTO user_genre_count (userId, genreCategoryName, numBooks)
            VALUES (?, ?, 1)
            ON CONFLICT (userId, genreCategoryName)
            DO UPDATE SET numBooks = user_genre_count.numBooks + 1;
            """;
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setInt(1, bookId);
            stmt.setString(2, genreCategory);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user_genre_count for recommendations: " + e.getMessage());
        }


    }


}
