/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.models;

/**
 * Represents a user's rating for a book in the library application.
 * This class serves as the base for rating-related entities.
 */
public class BasicRating {

    /**
     * Unique identifier for the rating.
     */
    private final String ratingId;

    /**
     * The rating value given by the user (1–5 stars).
     */
    private final int rating;

    /**
     * The date when the rating was submitted.
     */
    private final String ratingDate;

    /**
     * The user who submitted the rating.
     */
    private final User user;

    /**
     * The book that was rated.
     */
    private final Book book;

    /**
     * Constructs a BasicRating with specified details.
     *
     * @param ratingId    unique ID of the rating
     * @param rating      rating value (1–5)
     * @param ratingDate  date the rating was created
     * @param user        user who submitted the rating
     * @param book        book that was rated
     */
    public BasicRating(String ratingId, int rating, String ratingDate, User user, Book book) {
        this.ratingId = ratingId;
        this.rating = rating;
        this.ratingDate = ratingDate;
        this.user = user;
        this.book = book;
    }

    public String getRatingId() {
        return ratingId;
    }

    public int getRating() {
        return rating;
    }

    public String getRatingDate() {
        return ratingDate;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }
}
