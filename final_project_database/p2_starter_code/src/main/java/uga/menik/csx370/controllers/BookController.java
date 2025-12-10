package uga.menik.csx370.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Book;
import uga.menik.csx370.models.Simple_Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.BookService;
import uga.menik.csx370.services.CheckoutService;
import uga.menik.csx370.services.UserService;
import uga.menik.csx370.services.WishlistService;

/**
 * Handles /books URL and suburl's
 */
@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final CheckoutService checkoutService;
    private final UserService userService;
    private final WishlistService wishlistService;

    @Autowired
    public BookController(BookService bookService, CheckoutService checkoutService, UserService userService, WishlistService wishlistService) {
        this.bookService = bookService;
	    this.checkoutService = checkoutService;
	    this.userService = userService;
        this.wishlistService = wishlistService;
    }

    @PostMapping("/{bookId}/checkout")
    public String checkoutBook(@PathVariable("bookId") int bookId) {
	try{
	    User user = userService.getLoggedInUser();
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
        ModelAndView mv = new ModelAndView("book_page");
        Book book = bookService.getBook(bookId);
        String formattedGenres = book.getGenres();
        formattedGenres = formattedGenres.replace("[", "");
        formattedGenres = formattedGenres.replace("]", "");
        formattedGenres = formattedGenres.replace("'", "");

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
    
    // wishlist button object for when you click on the book 
    boolean isWishlisted = false; // by default the book is not wishlisted
    String wishlistButtonText = "Add to Wishlist"; // default button text
    try {
        User user = userService.getLoggedInUser();
        isWishlisted = wishlistService.isWishlisted(user.getUserId(), bookId);
        if (isWishlisted) {
            wishlistButtonText = "Already in Wishlist";
        }
    } catch (Exception e) {
        e.printStackTrace();
    } 
    mv.addObject("isWishlisted", isWishlisted);
    mv.addObject("wishlistButtonText", wishlistButtonText);

    // add "Users who liked this also liked" section
    List<Simple_Book> booksAlsoLiked = bookService.youMayAlsoLike(bookId);
    mv.addObject("booksAlsoLiked", booksAlsoLiked);
        
	return mv;
    }    


}
