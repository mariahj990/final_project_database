package uga.menik.csx370.models;
import java.sql.Date;


/**
 * Represents a book in the library system.
 */
public class CheckedOutBook {
    private final int bookId;
    private final String title;
    private final String authors;
    private final double average_rating;
    private final Date checked_out_date;
    private final Date due_date;

    public CheckedOutBook(int bookId, String title, String authors, double average_rating, Date checked_out_date, Date due_date) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.average_rating = average_rating;
        this.checked_out_date = checked_out_date;
        this.due_date = due_date;
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

    public Date getDue_date() {
        return due_date;
    }
    public Date getChecked_out_date() {
        return checked_out_date;
    }
}
