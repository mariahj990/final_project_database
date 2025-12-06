package uga.menik.csx370.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uga.menik.csx370.models.User;
import uga.menik.csx370.services.CheckoutService;
import uga.menik.csx370.services.UserService;

@Controller
@RequestMapping("/return")
public class ReturnController {
    
    private final CheckoutService checkoutService;
    private final UserService userService;

    @Autowired
    public ReturnController(CheckoutService checkoutService, UserService userService) {
        this.checkoutService = checkoutService;
        this.userService = userService;
    }

    @PostMapping
    public String returnBook(@RequestParam("bookId") int bookId) {
        try {
            User user = userService.getLoggedInUser();
            System.out.println("User " + user.getUserId() + " returning book " + bookId);
            checkoutService.returnBook(user, bookId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "redirect:/checkout";
    }
}