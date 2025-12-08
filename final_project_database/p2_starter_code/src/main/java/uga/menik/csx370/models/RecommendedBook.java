package uga.menik.csx370.models;

/**
 * Represents a book in the library system.
 */
public class RecommendedBook {
    private final int bookId;
    private final String title;
    private final String authors;
    private final double average_rating;
    private final String recommended_genre;
    private final String image_url;

    public RecommendedBook(int bookId, String title, String authors, double average_rating, String recommended_genre, String image_url) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.average_rating = average_rating;
        this.recommended_genre = recommended_genre;
        this.image_url = image_url;
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

    public String getImage_url() {
        return image_url;
    }

}
