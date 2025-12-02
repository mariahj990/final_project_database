package uga.menik.csx370.models;

/**
 * Represents a book in the library system.
 */
public class Book {

    private final String bookId;
    private final String title;
    private final String author;
    private final String genre;

    public Book(String bookId, String title, String author, String genre) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }
}
