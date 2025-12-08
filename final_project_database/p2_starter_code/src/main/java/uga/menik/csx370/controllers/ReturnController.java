package uga.menik.csx370.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.BookService;
import uga.menik.csx370.services.CheckoutService;
import uga.menik.csx370.services.UserService;

@Controller
@RequestMapping("/return")
public class ReturnController {
    
    private final CheckoutService checkoutService;
    private final UserService userService;
    private final BookService bookService;

    @Autowired
    public ReturnController(CheckoutService checkoutService, UserService userService, BookService bookService) {
        this.checkoutService = checkoutService;
        this.userService = userService;
        this.bookService = bookService;

    }
    /**
     * Returns the book and redirects to the rating page. 
     */
    @PostMapping
    public String returnBook(@RequestParam("bookId") int bookId) {
        try {
            User user = userService.getLoggedInUser();
            System.out.println("User " + user.getUserId() + " returning book " + bookId);
            
            boolean success = checkoutService.returnBook(user, bookId);
            
            if (success) {
                // Redirect to rating page for this book
                return "redirect:/return/rate?bookId=" + bookId;
            } else {
                return "redirect:/checkout";
            }
        } catch (SQLException e) {
            System.out.println("There was an issue returning the book" + e);
            return "redirect:/checkout";
        }
    }

    /**
     * Show the rating page
     */
    @GetMapping("/rate")
    public ModelAndView showRatingPage(@RequestParam("bookId") int bookId) {
        ModelAndView mv = new ModelAndView("rating_page");
        
        try {
            Book book = bookService.getBook(bookId);
            mv.addObject("bookId", book.getBookId());
            mv.addObject("title", book.getTitle());
            mv.addObject("authors", book.getAuthors());
            mv.addObject("image_url", book.getImage_url());
        } catch (Exception e) {
            System.out.println("There was an issue rating the book" + e);
            return new ModelAndView("redirect:/checkout");
        }
        
        return mv;
    }
}