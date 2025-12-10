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
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.CheckedOutBook;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.CheckoutService;
import uga.menik.csx370.services.UserService;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserService userService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService, UserService userService) {
        this.checkoutService = checkoutService;
        this.userService = userService;
    }

    // Show all checkouts for the logged-in user
    @GetMapping
    public ModelAndView myCheckouts() {
        ModelAndView mv = new ModelAndView("checkout_page");
        User user = userService.getLoggedInUser();
        // get list of all users checked out books
	try{
	    List<CheckedOutBook> checkedOutBooks = checkoutService.getUsersCheckedOutBooks(user);
	    mv.addObject("checkouts", checkedOutBooks);
	} catch(SQLException e){
	    e.printStackTrace();
	    mv.addObject("checkouts", new ArrayList<>()); // add empty list
	}
        return mv;
    }

    
}


