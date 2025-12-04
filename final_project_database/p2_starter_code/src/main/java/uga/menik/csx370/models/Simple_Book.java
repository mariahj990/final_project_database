package uga.menik.csx370.models;

/**
 * Represents a book in the library system.
 */
public class Simple_Book {
    private final int bookId;
    private final String title;
    private final String authors;
    private final double average_rating;

    public Simple_Book(int bookId, String title, String authors, double average_rating) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.average_rating = average_rating;
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

    public double getAverage_rating() {
        return average_rating;
    }
}
