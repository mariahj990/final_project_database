package uga.menik.csx370.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.menik.csx370.models.User;

@Service
public class TopReadersService {
    private final DataSource dataSource;

    @Autowired
    public TopReadersService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This function queries and returns all users.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        // SQL query to get all users
        final String getAllUsersSql = "SELECT u.userId, u.firstName, u.lastName FROM user u";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getAllUsersSql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getString("userId"),
                        rs.getString("firstName"),
                        rs.getString("lastName")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return users;
    }
}