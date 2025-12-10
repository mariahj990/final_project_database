/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.csx370.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.services.BookService;

/**
 * Handles /search sub urls
 */
@Controller
public class SearchController {

    private final BookService bookService;

    @Autowired
    public SearchController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public ModelAndView searchBooks(@RequestParam("query") String query) {
        ModelAndView mv = new ModelAndView("books_search_page");
        List<Book> books = bookService.searchBooks(query);

        if (books.isEmpty()) {
            System.out.println("No results found for: " + query);
            mv.addObject("isNoContent", true);
        } else {
            mv.addObject("books", books);
        }

        mv.addObject("searchTerm", query);
        return mv;
    }
}
