package uga.menik.csx370.controllers;

public class SearchController {
    
}
package uga.menik.csx370.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.BookService;
import uga.menik.csx370.models.Book;

import java.util.List;

/**
 * Handles search functionality for books.
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
        System.out.println("Searching for books with keyword: " + query);

        ModelAndView mv = new ModelAndView("books_search_page");
        List<Book> books = bookService.searchBooks(query);

        mv.addObject("books", books);
        mv.addObject("searchTerm", query);
        mv.addObject("isNoContent", books.isEmpty());

        return mv;
    }
}
