/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.models;

/**
 * Represents a book in the library system.
 */
public class Book {
    private final int bookId;
    private final String title;
    private final String authors;
    private final String genres;
    private final String isbn13;
    private final String description;
    private final double average_rating;
    private final int original_publication_year;
    private final int ratings_count;
    private final String image_url;
    private final int total_copies;
    private final int page_count;

    public Book(int bookId, String title, String authors, String isbn13, String description, 
                String genres, double average_rating, int original_publication_year,
                int ratings_count, String image_url, int total_copies, int page_count) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.isbn13 = isbn13;
        this.description = description;
        this.genres = genres;
        this.average_rating = average_rating;
        this.original_publication_year = original_publication_year;
        this.ratings_count = ratings_count;
        this.image_url = image_url;
        this.total_copies = total_copies;
        this.page_count = page_count;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getisbn13() {
        return isbn13;
    }

    public String getDescription() {
        return description;
    }

    public String getGenres() {
        return genres;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public int getOriginal_publication_year() {
        return original_publication_year;
    }

    public int getRatings_count() {
        return ratings_count;
    }

    public String getImage_url() {
        return image_url;
    }

    public int getTotal_copies() {
        return total_copies;
    }

    public int getPage_count() {
        return page_count;
    }
}
