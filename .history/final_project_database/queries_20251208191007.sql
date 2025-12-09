-- Purpose: Retrieve the user's basic info (ID, first name, last name)
-- Context: AccountService.java
-- URL: http://localhost:8080/account
final String getUserSql = "SELECT userId, firstName, lastName FROM user WHERE userId = ?";


-- Purpose: Count how many books the user has wishlisted
-- Context: AccountService.java
-- URL: http://localhost:8080/account
final String getNumWishlist = "SELECT COUNT(*) FROM history WHERE userId = ? AND has_wishlisted = 1;";


-- Purpose: Retrieve all wishlisted books for the user
-- Context: AccountService.java
-- URL: http://localhost:8080/account
final String getWishlistBooks = "SELECT b.bookId, b.title, b.authors, b.isbn13, " +
                                "b.description, b.genres, b.average_rating, " +
                                "b.original_publication_year, b.ratings_count," +
                                "b.image_url, b.total_copies, b.page_count " +
                            "FROM history AS his " +
                            "JOIN book AS b ON b.bookId = his.bookId " +
                            "WHERE his.userId = ?;";


-- Purpose: Count how many books the user has currently checked out
-- Context: AccountService.java
-- URL: http://localhost:8080/account
final String getNumWishlist = "SELECT COUNT(*) AS NumCheckedOut FROM curr_checkout GROUP BY userId HAVING userId = ?";


-- Purpose: Count how many books the user has read
-- Context: AccountService.java
-- URL: http://localhost:8080/account
final String getNumBooksRead = "SELECT COUNT(*) FROM history WHERE userId = ? AND has_read = 1;";


-- Purpose: Retrieve total pages read by the user
-- Context: AccountService.java
-- URL: http://localhost:8080/account
final String getNumPagesRead = "SELECT page_count FROM history JOIN book ON history.bookId = book.bookId WHERE userId = ? AND has_read = 1;";

-- Purpose: Retrieve details of a specific book by its ID
-- Context: BookService.java
-- URL: http://localhost:8080/books/{bookId}
final String getAllBooks = "SELECT * FROM book where bookId = ?";


-- Purpose: Check how many total copies exist for a given book
-- Context: BookService.java
-- URL: http://localhost:8080/books/{bookId}
final String checkAvailability = "SELECT total_copies from book where bookId = ?";


-- Purpose: Get number of users who currently have the book checked out
-- Context: BookService.java
-- URL: http://localhost:8080/books/{bookId}
final String getNumCheckedOut = "SELECT COUNT(distinct userId) as numCheckedOut from curr_checkout where bookId = ?";


-- Purpose: Search books by keyword in title, author, or genre
-- Context: BookService.java
-- URL: http://localhost:8080/search?query=<keyword>
String sql = """
            SELECT * FROM book 
            WHERE LOWER(title) LIKE ? 
            OR LOWER(authors) LIKE ? 
            OR LOWER(genres) LIKE ?
            """;

-- Purpose: Check if the current user already has this book checked out
-- Context: CheckoutService.java
-- URL: http://localhost:8080/books/{bookId}
final String querySql = "select count(*) as count_thisBookandUser from curr_checkout where userId = ? and bookId = ?";


-- Purpose: Retrieve all book IDs and checkout dates for the current user
-- Context: CheckoutService.java
-- URL: http://localhost:8080/account
final String getBookIds = "SELECT bookId, checkout_date from curr_checkout where userId = ?"; //get all book Ids


-- Purpose: Insert a new checkout record for the user and book
-- Context: CheckoutService.java
-- URL: http://localhost:8080/books/{bookId}/checkout
final String checkout = "insert into curr_checkout (userId, bookId, checkout_date) values (?, ?, CURDATE())";


-- Purpose: Delete a checkout record when a user returns a book
-- Context: CheckoutService.java
-- URL: http://localhost:8080/books/{bookId}/return
final String deleteCheckout = "delete from curr_checkout where userId = ? and bookId = ?";


-- Purpose: Update the history table to mark a book as read
-- Context: CheckoutService.java
-- URL: http://localhost:8080/books/{bookId}/return
final String updateHistory = "insert into history (userId, bookId, has_read) values (?, ?, true) on duplicate key update has_read = true";


-- Purpose: Update the user's total number of books and pages read
-- Context: CheckoutService.java
-- URL: http://localhost:8080/books/{bookId}/return
final String updateUser = "update user set num_books_read = num_books_read + 1, num_pages_read = num_pages_read + ? where userId = ?";

-- Purpose: Check if the book is already present in the user’s history table
-- Context: ForYouPageService.java
-- URL: http://localhost:8080/foryou
final String querySql = "select exists(select 1 from history where userId = ? and bookId = ? ) as found_in_history;";


-- Purpose: Check if the book is already checked out by the user
-- Context: ForYouPageService.java
-- URL: http://localhost:8080/foryou
final String querySql2 = "select exists(select 1 from curr_checkout where userId = ? and bookId = ? ) as found_in_current_checkout;";


-- Purpose: Retrieve 20 recommended books for the user based on their genre history
-- Context: ForYouPageService.java
-- URL: http://localhost:8080/foryou
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


-- Purpose: Find the top genre for a specific book based on the user’s reading patterns
-- Context: ForYouPageService.java
-- URL: http://localhost:8080/foryou
final String findTopGenre = "SELECT gc.genreCategoryName " +
        "FROM book_to_genre btg " +
        "JOIN genre_category gc ON btg.genreName = gc.genreName " +
        "LEFT JOIN user_genre_count ugc " +
                "ON ugc.genreCategoryName = gc.genreCategoryName " +
                  "AND ugc.userId = ? " +
        "WHERE btg.bookId = ? " +
        "ORDER BY ugc.numBooks DESC " +
        "LIMIT 1;";


-- Purpose: Retrieve all genre categories for a specific book
-- Context: ForYouPageService.java
-- URL: http://localhost:8080/foryou
final String findGenresForBook = """
            SELECT gc.genreCategoryName
            FROM book_to_genre as btg
            JOIN genre_category as gc
              ON btg.genreName = gc.genreName
            WHERE bookId = ?;
            """;


-- Purpose: Update the user's genre count based on book interactions
-- Context: ForYouPageService.java
-- URL: http://localhost:8080/foryou
final String updateSql = """
            INSERT INTO user_genre_count (userId, genreCategoryName, numBooks)
            VALUES (?, ?, 1)
            ON DUPLICATE KEY UPDATE numBooks = numBooks + 1;
            """;
-- Purpose: Retrieve the genre ID based on the genre name
-- Context: GenreService.java
-- URL: http://localhost:8080/foryou
final String selectSql = "select genre_id from genre where genre_name = ?";


-- Purpose: Insert or update the user’s genre count
-- Context: GenreService.java
-- URL: http://localhost:8080/foryou
final String upsertSql = """
            insert into user_genre_count (user_id, genre_id, count)
            values (?, ?, 1)
            on duplicate key update count = count + 1
        """;

-- Purpose: Insert a new rating or update an existing rating by the same user for a book
-- Context: RatingService.java
-- URL: http://localhost:8080/books/{bookId}/rate
final String newRating = """
            insert into ratings (userId, bookId, rating)
            values (?, ?, ?)
            on duplicate key update rating = ?
        """;


-- Purpose: Retrieve the current average rating and total number of ratings for a given book
-- Context: RatingService.java
-- URL: http://localhost:8080/books/{bookId}
final String getBookStats = "select average_rating, ratings_count from book where bookId = ?";


-- Purpose: Update a book’s average rating and ratings count after a new rating is added
-- Context: RatingService.java
-- URL: http://localhost:8080/books/{bookId}/rate
final String updateBook = "update book set average_rating = ?, ratings_count = ? where bookId = ?";


-- Purpose: Retrieve an existing rating a user has given to a specific book
-- Context: RatingService.java
-- URL: http://localhost:8080/books/{bookId}/rate
final String query = "select rating from ratings where userId = ? and bookId = ?";

-- Purpose: Retrieve the top 10 books ordered by average rating
-- Context: TrendingService.java
-- URL: http://localhost:8080/trending
final String getAllBooks = "SELECT * FROM book ORDER BY average_rating LIMIT 10;";


-- Purpose: Retrieve the top 10 users who have read the most books
-- Context: TrendingService.java
-- URL: http://localhost:8080/trending
final String getTopUsersSql = "SELECT u.userId, u.firstName, u.lastName, COUNT(*) AS numRead " + 
                                        "FROM user AS u " + 
                                        "JOIN history AS h ON u.userId = h.userId " + 
                                        "WHERE h.has_read = 1 " + 
                                        "GROUP BY u.userId, u.firstName, u.lastName " + 
                                        "ORDER BY numRead DESC LIMIT 10;";


-- Purpose: Retrieve user details (first name, last name) for a specific user ID
-- Context: TrendingService.java
-- URL: http://localhost:8080/trending
final String getUserSql = "SELECT userId, firstName, lastName FROM user where userId = ?";


-- Purpose: Count how many distinct users have read at least one book
-- Context: TrendingService.java
-- URL: http://localhost:8080/trending
final String getNumReadTot = "SELECT COUNT(DISTINCT u.userId) AS numReadTot "
                                        + "FROM user AS u "
                                        + "JOIN history AS h ON u.userId = h.userId "
                                        + "WHERE h.has_read = 1;";
