/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Simple_Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.TrendingService;
import uga.menik.csx370.services.UserService;

/**
 * Handles /trending URL and it's sub URLs.
 */
@Controller
@RequestMapping("/trending")
public class TrendingController {

    // UserService has user login and registration related functions.
    private final TrendingService trendingService;

    /**
     * See notes in AuthInterceptor.java regarding how this works 
     * through dependency injection and inversion of control.
     */
    @Autowired
    public TrendingController(TrendingService trendingService, UserService userService) {
        this.trendingService = trendingService;

    }

    /**
     * This function handles /trending URL itself.
     * This serves the webpage that shows __.
     */
    @GetMapping
    public ModelAndView showTrendingPage() {
        ModelAndView mv = new ModelAndView("trending_page");
 
        List<User> users = trendingService.getTop10Users();
        mv.addObject("users", users);

        int numTop10Users = trendingService.getNumTop10Users();
        mv.addObject("numTop10Users", numTop10Users);

        List<Simple_Book> books = trendingService.getTop10Books();
        mv.addObject("books", books);
        if(books.isEmpty()) {
            System.out.println("Books is empty");
            mv.addObject("isNoContent", true);
        }
        return mv;
    }

    @GetMapping("/trendingUser/{userId}")
    public ModelAndView profileOfSpecificTopUser(@PathVariable("userId") int userId) {
        User user = trendingService.getUserById(userId);
        ModelAndView mv = new ModelAndView("user_profile_page");
        mv.addObject("firstName", user.getFirstName());
        mv.addObject("lastName", user.getLastName());
        mv.addObject("userId", user.getUserId());
        mv.addObject("profileImagePath", user.getProfileImagePath());
        mv.addObject("userNumBooksRead", trendingService.getUserNumBooksRead(userId));
        
        return mv;
    } //profileOfSpecificTopUser

}