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
