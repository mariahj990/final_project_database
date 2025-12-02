package uga.menik.csx370.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.User;
import uga.menik.csx370.services.TopReadersService;

@Controller
@RequestMapping("/topreaders")
public class TopReadersController {
    
    private final TopReadersService topReadersService;

    @Autowired
    public TopReadersController(TopReadersService topReadersService) {
        this.topReadersService = topReadersService;
    }

    @GetMapping
    public ModelAndView webpage() {
        // top_readers_page is a mustache template from src/main/resources/templates.
        // ModelAndView class enables initializing one and populating placeholders
        // in the template using Java objects assigned to named properties.
        ModelAndView mv = new ModelAndView("top_readers_page");
        
        // Get all users
        List<User> allUsers = topReadersService.getAllUsers();
        
        // Add users to the model
        mv.addObject("users", allUsers);
        
        return mv;
    }
}