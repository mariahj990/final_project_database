package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.models.Simple_Book;
import uga.menik.csx370.models.User;
import uga.menik.csx370.services.UserService;
import uga.menik.csx370.services.WishlistService;


/**
 * Handles /wishlist URL.
 */
@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private UserService userService;

    @Autowired
    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * This function handles /wishlist URL itself.
     * This serves the webpage that shows wishlisted books by logged in user.
     */
    @GetMapping
    public ModelAndView showWishlistPage() {
        System.out.println("User is attempting to view the wishlist page");
        ModelAndView mv = new ModelAndView("wishlist_page");
 
        List<Simple_Book> books = wishlistService.getAllWhishlist();
        mv.addObject("books", books);
        if(books.isEmpty()) {
            System.out.println("Books is empty");
            mv.addObject("isNoContent", true);
        } else {
            System.out.println("showWishlistPage worked");
        }
        return mv;
    } // showWishlistPage
    
    /**
     * This function handles the /removeWishlist URL.
     * This removes a book from the wishlist of the logged in user.
     */
    @GetMapping("/{bookId}/wishlisted/{isAdd}") 
    public String handleWishlistAction(@PathVariable("bookId") int bookId, 
                                      @PathVariable("isAdd") boolean isAdd) {

        User currentUser = userService.getLoggedInUser(); // get the logged in User
        String loggedInUserId = currentUser.getUserId(); // get the logged in user's id 

        System.out.println("wishlist action -> book: " + bookId + ", add: " + isAdd);
        boolean done;

        if (isAdd) {
            done = wishlistService.addWishlist(loggedInUserId, bookId);
        } else {
            done = wishlistService.removeWishlist(loggedInUserId, bookId);
        } //if-else

        if (done) {
            return "redirect:/wishlist";
        } //if

        String err = URLEncoder.encode("couldn't update wishlist, try again.", StandardCharsets.UTF_8);
        return "redirect:/wishlist?error=" + err;
        
    } // handleWishlistAction

}
