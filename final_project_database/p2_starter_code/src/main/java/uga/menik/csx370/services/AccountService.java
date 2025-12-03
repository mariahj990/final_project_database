package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.User;

/*
 * This service contains Account related functions. 
 */
@Service
public class AccountService {
    private final DataSource dataSource;
    private final UserService userService;

    @Autowired
    public AccountService(DataSource datasource, UserService userService) {
        this.dataSource = datasource;
        this.userService = userService;
    } //BookmarksService

    public User getCurrentUser() {
        String userId = userService.getLoggedInUser().getUserId();
        System.out.println("getCurrentUser: " + userId);
        
        // SQL query to get all users
        final String getUserSql = "SELECT u.userId, u.firstName, u.lastName FROM user u WHERE u.userID = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getUserSql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    // Initialize and retain the logged in user.
                    User loggedInUser = new User(userId, firstName, lastName);
                    return loggedInUser;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        System.out.println("User not found.");
        return null;

    }


}
