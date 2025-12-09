-- DDL FILE: ddl.sql
-- Defines all tables used in the Library Web App

create database if not exists cs4370_final_library;
use cs4370_final_library;

-- user table
create table if not exists user (
    userId int auto_increment primary key,
    username varchar(255) not null unique,
    password varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    num_pages_read int default 0,
    num_books_read int default 0
);

-- book table
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

-- ratings table
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

-- current checkout table
create table if not exists curr_checkout (
    userId int, 
    bookId int, 
    checkout_date date not null,
    primary key (userId, bookId), 
    foreign key (userId) references user(userId),
    foreign key (bookId) references book(bookId)
);

-- history table
create table if not exists history (
    wishlist_id int auto_increment,
    bookId int, 
    userId int, 
    has_wishlisted boolean not null default false,
    has_read boolean not null default false, 
    unique (bookId, userId),
    primary key (wishlist_id), 
    foreign key (bookId) references book(bookId),
    foreign key (userId) references user(userId)
);

-- genre category table
create table if not exists genre_category (
    genreName varchar(100),
    genreCategoryName varchar(100) not null,
    primary key (genreName)
);

-- book to genre mapping table
create table if not exists book_to_genre (
    bookId int,
    genreName varchar(100),
    primary key (bookId, genreName),
    foreign key (bookId) references book(bookId),
    foreign key (genreName) references genre_category(genreName)
);

-- user genre count table
create table if not exists user_genre_count (
    userId int,
    genreCategoryName varchar(100),
    numBooks int default 0,
    primary key (userId, genreCategoryName),
    foreign key (userId) references user(userId)
);

-- csv data loading status table
create table if not exists csv_data_loading_status (
    insertedId int,
    ran boolean not null default 0,
    primary key (insertedId)
);
