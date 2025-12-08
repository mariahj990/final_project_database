/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.RecommendedBook;
import uga.menik.csx370.models.Simple_Book;
import uga.menik.csx370.services.ForYouPageService;
import uga.menik.csx370.services.UserService;

/**
 * This controller handles the home page and some of it's sub URLs.
 */
@Controller
@RequestMapping
public class HomeController {

    @Autowired
    private final UserService userService;
    private final ForYouPageService forYouPageService;

    public HomeController(UserService userService, ForYouPageService forYouPageService) {
        this.userService = userService;
        this.forYouPageService = forYouPageService;
    }

    /**
     * This is the specific function that handles the root URL itself.
     * 
     * Note that this accepts a URL parameter called error.
     * The value to this parameter can be shown to the user as an error message.
     * See notes in HashtagSearchController.java regarding URL parameters.
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) throws SQLException{
        ModelAndView mv = new ModelAndView("home_page");
        System.out.println("Trying to view home page.");
        List<Simple_Book> simple_books = forYouPageService.getCandidateBooks();
	    List<RecommendedBook> books = new ArrayList<>();
        for (Simple_Book book : simple_books) {
            // want to add another attribute to each book: what the top genre for the match was
            String topGenre = forYouPageService.getTopMatchingGenreForBook(book.getBookId());
            RecommendedBook recBook = new RecommendedBook(book.getBookId(), book.getTitle(), book.getAuthors(), book.getAverage_rating(), topGenre, book.getImage_url());
            System.out.println("recommended url: " + book.getImage_url());
            books.add(recBook);
        }
        mv.addObject("books", books);
        return mv;
    }

 

}
