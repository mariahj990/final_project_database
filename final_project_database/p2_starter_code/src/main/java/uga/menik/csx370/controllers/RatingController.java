package uga.menik.csx370.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uga.menik.csx370.models.User;
import uga.menik.csx370.services.RatingService;
import uga.menik.csx370.services.UserService;

@Controller
@RequestMapping("/rating")
public class RatingController {
    
    private final RatingService ratingService;
    private final UserService userService;

    @Autowired
    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    /**
     * Submit a rating for a book
     */
    @PostMapping("/submit")
    public String submitRating(@RequestParam("bookId") int bookId, @RequestParam("rating") int rating) {
        try {
            User user = userService.getLoggedInUser();
            
            ratingService.rateBook(user.getUserId(), bookId, rating);
        } catch (SQLException e) {
            System.out.print("There was an error submitting the rating" + e);
        }
        
        return "redirect:/checkout";
    }

    /**
     * Skip rating and go back to checkout
     */
    @PostMapping("/skip")
    public String skipRating() {
        return "redirect:/checkout";
    }
}