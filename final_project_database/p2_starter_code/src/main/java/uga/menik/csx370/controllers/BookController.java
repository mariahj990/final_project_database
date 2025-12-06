package uga.menik.csx370.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.BookService;
import uga.menik.csx370.services.CheckoutService;
import uga.menik.csx370.services.UserService;
/**
 * Handles /books URL and suburl's
 */
@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final CheckoutService checkoutService;
    private final UserService userService;

    @Autowired
    public BookController(BookService bookService, CheckoutService checkoutService, UserService userService) {
        this.bookService = bookService;
	    this.checkoutService = checkoutService;
	    this.userService = userService;
    }

    @PostMapping("/{bookId}/checkout")
    public String checkoutBook(@PathVariable("bookId") int bookId) {
	try{
	    User user = userService.getLoggedInUser();
	    System.out.println("Checking out bookId: " + bookId + " for user: " + user.getUserId());
	    checkoutService.checkOutBook(user, bookId); // performs the actual checkout
	} catch(SQLException e){
	    e.printStackTrace();
	}
        return "redirect:/checkout";           
    }

    /**
     * This function handles /books URL itself.
     */
    @GetMapping("/{bookId}")
    public ModelAndView showTrendingPage(@PathVariable("bookId") int bookId) {
        System.out.println("User is attempting to view the book page for book w/ id: " + bookId);
        ModelAndView mv = new ModelAndView("book_page");
        Book book = bookService.getBook(bookId);
        String formattedGenres = book.getGenres();
        formattedGenres = formattedGenres.replace("[", "");
        formattedGenres = formattedGenres.replace("]", "");
        formattedGenres = formattedGenres.replace("'", "");
        System.out.println("Genres: " + book.getGenres());
        System.out.println("Fixed: " + formattedGenres);

        mv.addObject("bookId", book.getBookId());
        mv.addObject("title", book.getTitle());
        mv.addObject("authors", book.getAuthors());
        mv.addObject("isbn13", book.getisbn13());
        mv.addObject("description", book.getDescription());
        mv.addObject("genres", formattedGenres);
        mv.addObject("average_rating", book.getAverage_rating());
        mv.addObject("original_publication_year", book.getOriginal_publication_year());
        mv.addObject("ratings_count", book.getRatings_count());
        mv.addObject("image_url", book.getImage_url());
        mv.addObject("total_copies", book.getTotal_copies());

	String buttonText;
	boolean isDisabled;
	try{
	    
	    User user = userService.getLoggedInUser();
	    if(checkoutService.isCheckedOutbyUserNow(user, book.getBookId())){
		buttonText = "Already Have Book";
		isDisabled = true;
	    } else if(bookService.getIfBookAvailable(book.getBookId()) == false){
		buttonText = "Book Unavailable";
		isDisabled = true;
	    } else{
		buttonText = "Check Out Book";
		isDisabled = false;
	    }
	} catch(SQLException e){
	    e.printStackTrace();
	    buttonText = "Error";
	    isDisabled = true;
	}
	mv.addObject("checkOutBook",buttonText);
        mv.addObject("isDisabled", isDisabled);
        
	return mv;
    }    

    @GetMapping("/search")
    public ModelAndView searchBooks(@RequestParam("q") String query) {
        System.out.println("Searching for books with keyword: " + query);

        ModelAndView mv = new ModelAndView("books_search_page");
        var books = bookService.searchBooks(query);

        if (books.isEmpty()) {
            mv.addObject("isNoContent", true);
        } else {
            mv.addObject("books", books);
        }

        return mv;
    }//search books

    

}
