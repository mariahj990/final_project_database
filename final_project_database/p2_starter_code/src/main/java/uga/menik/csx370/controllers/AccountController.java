package uga.menik.csx370.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ModelAndView webpage() {
        // posts_page is a mustache template from src/main/resources/templates.
        // ModelAndView class enables initializing one and populating placeholders
        // in the template using Java objects assigned to named properties.
        ModelAndView mv = new ModelAndView("account_page");
        System.out.println("Trying to view account page");
        // Get user

        User user = accountService.getCurrentUser();
        String name = user.getFirstName() +  " " + user.getLastName();
        System.out.println("name: " + name);

        int numWishlist = accountService.getCurrentUserNumWishlist();
        System.out.println("numWishlist: " + numWishlist);

        List<Book> currentUserWishlist = accountService.getCurrentUserWishlist();

        // Add users to the model
        mv.addObject("name", name);
        mv.addObject("user", user);
        mv.addObject("numWishlist", numWishlist);
        mv.addObject("currentUserWishlist", currentUserWishlist);
        return mv;
    }
}

