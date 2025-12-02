/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.models;

/**
 * Represents the basic structure of a post in the micro blogging platform.
 * This class serves as a base for both posts and comments.
 */
public class BasicPost {
    
    /**
     * Unique identifier for the post.
     */
    private final String postId;

    /**
     * Text content of the post.
     */
    private final String content;

    /**
     * Date when the post was created.
     */
    private final String postDate;

    /**
     * User who created the post.
     */
    private final User user;

    /**
     * Constructs a BasicPost with specified details.
     *
     * @param postId     the unique identifier of the post
     * @param content    the text content of the post
     * @param postDate   the creation date of the post
     * @param user       the user who created the post
     */
    public BasicPost(String postId, String content, String postDate, User user) {
        this.postId = postId;
        this.content = content;
        this.postDate = postDate;
        this.user = user;
    }

    /**
     * Returns the post ID.
     *
     * @return the post ID
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Returns the content of the post.
     *
     * @return the content of the post
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the post creation date.
     *
     * @return the post creation date
     */
    public String getPostDate() {
        return postDate;
    }

    /**
     * Returns the user who created the post.
     *
     * @return the user who created the post
     */
    public User getUser() {
        return user;
    }
}



/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/

package uga.menik.csx370.models;

/**
 * Represents the basic structure of a review in the library application.
 * This class serves as a base for all review-related entities.
 */
public class BasicReview {

    /**
     * Unique identifier for the review.
     */
    private final String reviewId;

    /**
     * Text content of the review written by the user.
     */
    private final String reviewText;

    /**
     * Date when the review was created.
     */
    private final String reviewDate;

    /**
     * The rating given to the book (e.g., 1–5 stars).
     */
    private final int rating;

    /**
     * The book being reviewed.
     */
    private final Book book;

    /**
     * User who created the review.
     */
    private final User user;

    /**
     * Constructs a BasicReview with specified details.
     *
     * @param reviewId    the unique identifier of the review
     * @param reviewText  the text content of the review
     * @param reviewDate  the creation date of the review
     * @param rating      the rating given to the book
     * @param book        the book that was reviewed
     * @param user        the user who wrote the review
     */
    public BasicReview(String reviewId, String reviewText, String reviewDate, int rating, Book book, User user) {
        this.reviewId = reviewId;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.book = book;
        this.user = user;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public int getRating() {
        return rating;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }
}
