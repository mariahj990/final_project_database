package uga.menik.csx370.models;

/**
 * Represents a book in the library system.
 */
public class Simple_Book {
    private final int bookId;
    private final String title;
    private final String authors;
    private final double average_rating;
    private final String recommended_genre;

    public RecommendedBook(int bookId, String title, String authors, double average_rating, String recommended_genre) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.average_rating = average_rating;
        this.recommended_genre = recommended_genre;
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
    public String getRecommended_genre() {
        return recommended_genre;
    }
}
