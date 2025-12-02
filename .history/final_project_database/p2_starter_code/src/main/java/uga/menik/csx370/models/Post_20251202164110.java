/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.models;

/**
 * Represents a user's rating for a book in the library application,
 * extending BasicRating to include additional fields if needed.
 */
public class Rating extends BasicRating {

    /**
     * Constructs a Rating with specified details including information from BasicRating.
     *
     * @param ratingId    unique ID of the rating
     * @param rating      value given by the user (1–5)
     * @param ratingDate  date the rating was created
     * @param user        the user who submitted the rating
     * @param book        the book that was rated
     */
    public Rating(String ratingId, int rating, String ratingDate, User user, Book book) {
        super(ratingId, rating, ratingDate, user, book);
    }
}
