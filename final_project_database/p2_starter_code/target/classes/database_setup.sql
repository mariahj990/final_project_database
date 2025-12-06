-- Create the database.
create database if not exists cs4370_final_library;

-- Use the created database.
use cs4370_final_library;

--user table
create table if not exists user (
    userId int auto_increment primary key,
    username varchar(255) not null unique,
    password varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    num_pages_read int default 0,
    num_books_read int default 0
    -- primary key (userId),
    -- unique (username),
    -- constraint userName_min_length check (char_length(trim(username)) >= 2),
    -- constraint firstName_min_length check (char_length(trim(firstName)) >= 2),
    -- constraint lastName_min_length check (char_length(trim(lastName)) >= 2)
);

--book table
-- R2 = R[["book_id", "isbn13", "description", "genres", "title", "authors", "average_rating", "original_publication_year", "ratings_count", "image_url", "total_copies"]].drop_duplicates().reset_index(drop=True)

create table if not exists book (
    bookId int auto_increment primary key, 
    title varchar(255) not null,
    authors varchar(255) not null,
    isbn13 varchar(13) not null unique,
    description text,
    genres varchar(255) not null,
    average_rating float,
    original_publication_year int,
    ratings_count int,
    image_url varchar(255),
    total_copies int not null,
    page_count int
);

create table if not exists ratings (
    rating_id int auto_increment, 
    bookId int not null, 
    userId int not null, 
    rating int not null, 
    primary key (rating_id),
    unique (bookId, userId),
    foreign key (bookId) references book(bookId),
    foreign key (userId) references user(userId)
);

create table if not exists curr_checkout (
    userId int, 
    bookId int, 
    checkout_date date not null,
    primary key (userId, bookId), 
    foreign key (userId) references user(userId),
    foreign key (bookId) references book(bookId)
);

create table if not exists history (
    wishlist_id int auto_increment,
    bookId int, 
    userId int, 
    has_wishlisted boolean not null default False,
    has_read boolean not null default False, 
    unique (bookId, userId),
    primary key (wishlist_id), 
    foreign key (bookId) references book(bookId),
    foreign key (userId) references user(userId)
);

create table if not exists genre (
    genre_id int auto_increment primary key,
    genre_name varchar(100) not null unique
);

insert into genre (genre_name) values
    ('Adult Fiction'),
    ('Classics & Literature'),
    ('Young Adult (YA)'),
    ('Children / Middle Grade'),
    ('Fantasy & Sci-Fi'),
    ('Mystery / Thriller / Crime'),
    ('Romance & Humor'),
    ('Nonfiction / Biography / History'),
    ('Education / School'),
    ('Religion / Spirituality'),
    ('Food & Cooking'),
    ('Health / Medicine'),
    ('Travel / Geography / Nature'),
    ('Business / Finance'),
    ('Self Help / Personal Development'),
    ('Arts / Music / Theatre'),
    ('International / Regional Literature'),
    ('Other / Rare')
on duplicate key update genre_name = genre_name;

create table if not exists user_genre_count (
    user_id int not null,
    genre_id int not null,
    count int default 0,
    primary key (user_id, genre_id),
    foreign key (user_id) references user(userId),
    foreign key (genre_id) references genre(genre_id)
);