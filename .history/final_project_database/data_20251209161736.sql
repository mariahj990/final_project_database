
USE cs4370_final_library;

-- users

INSERT INTO user (username, password, firstName, lastName, num_pages_read, num_books_read)
VALUES
('alice', 'password123', 'Alice', 'Johnson', 120, 1),
('bob', 'password123', 'Bob', 'Smith', 450, 3),
('charlie', 'password123', 'Charlie', 'Brown', 0, 0);

 
-- books
 
INSERT INTO book (title, authors, isbn13, description, genres, average_rating, original_publication_year, ratings_count, image_url, total_copies, page_count)
VALUES
('Pride and Prejudice', 'Jane Austen', '9781111111111', 'A classic romantic novel.', 'Romance, Classics', 4.7, 1813, 10000, 'img1.jpg', 5, 250),
('The Hobbit', 'J.R.R. Tolkien', '9782222222222', 'A fantasy adventure story.', 'Fantasy, Adventure', 4.8, 1937, 20000, 'img2.jpg', 4, 300),
('To Kill a Mockingbird', 'Harper Lee', '9783333333333', 'A novel about racial injustice.', 'Drama, Historical', 4.9, 1960, 18000, 'img3.jpg', 3, 281);

 
-- RATINGS
 
INSERT INTO ratings (bookId, userId, rating)
VALUES
(1, 1, 5),
(2, 2, 4),
(3, 1, 5);

 
-- CURRENT CHECKOUT
 
INSERT INTO curr_checkout (userId, bookId, checkout_date)
VALUES
(1, 2, CURDATE()),  -- Alice checked out The Hobbit
(2, 3, CURDATE());  -- Bob checked out To Kill a Mockingbird

 
-- HISTORY (Wishlist + Read)
 
INSERT INTO history (bookId, userId, has_wishlisted, has_read)
VALUES
(1, 1, TRUE, FALSE),   -- Alice wishlisted Pride and Prejudice
(2, 1, FALSE, TRUE),   -- Alice read The Hobbit
(3, 2, TRUE, TRUE);    -- Bob wishlisted and read To Kill a Mockingbird

 
-- GENRE CATEGORIES
 
INSERT INTO genre_category (genreName, genreCategoryName)
VALUES
('Romance', 'Romance & Humor'),
('Fantasy', 'Fantasy & Sci-Fi'),
('Drama', 'Classics & Literature');

 
-- BOOK → GENRE MAPPING
 
INSERT INTO book_to_genre (bookId, genreName)
VALUES
(1, 'Romance'),
(2, 'Fantasy'),
(3, 'Drama');

 
-- USER GENRE COUNT
 
INSERT INTO user_genre_count (userId, genreCategoryName, numBooks)
VALUES
(1, 'Fantasy & Sci-Fi', 1),
(1, 'Romance & Humor', 1),
(2, 'Classics & Literature', 2);

 
-- CSV DATA LOADING STATUS
 
INSERT INTO csv_data_loading_status (insertedId, ran)
VALUES
(1, TRUE);
