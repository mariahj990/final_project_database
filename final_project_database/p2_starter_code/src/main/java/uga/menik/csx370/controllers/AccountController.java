/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Simple_Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.AccountService;

/**
 * Handles /account URL
 */
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
        ModelAndView mv = new ModelAndView("account_page");
        // Get user

        User user = accountService.getCurrentUser();
        String name = user.getFirstName() +  " " + user.getLastName();

        int numWishlist = accountService.getCurrentUserNumWishlist();

        int numCheckout = accountService.getCurrentUserNumCheckout();
        int numBooksRead = accountService.getCurrentUserNumBooksRead();
        int numPagesRead = accountService.getCurrentUserNumPagesRead();

        String topGenre = accountService.getTopGenre();
        //List<Book> currentUserWishlist = accountService.getCurrentUserWishlist();
        List<Simple_Book> userHistory = accountService.getUserHistory();

        mv.addObject("name", name);
        mv.addObject("user", user);
        mv.addObject("numWishlist", numWishlist);
        //mv.addObject("currentUserWishlist", currentUserWishlist);
        mv.addObject("numCheckout", numCheckout);
        mv.addObject("numBooksRead", numBooksRead);
        mv.addObject("numPagesRead", numPagesRead);
        mv.addObject("topGenre", topGenre);
        mv.addObject("userHistory", userHistory);
        return mv;
    }
}

