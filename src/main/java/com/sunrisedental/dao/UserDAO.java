package com.sunrisedental.dao;

import com.sunrisedental.model.User;
import java.util.List;

/**
 * Data Access Object interface for User operations in Sunrise Dental Clinic System.
 */
public interface UserDAO {

    /**
     * Authenticates staff member credentials against the database.
     *
     * @param username user login handle
     * @param password plain text password to verify
     * @return User object if credentials are valid; null otherwise
     */
    User authenticate(String username, String password);

    /**
     * Inserts a new user record.
     *
     * @param user user entity to persist
     * @return true if inserted successfully, false otherwise
     */
    boolean createUser(User user);

    /**
     * Finds a user by their unique username.
     *
     * @param username username to look up
     * @return User if found, null otherwise
     */
    User getUserByUsername(String username);

    /**
     * Finds a user by their primary key.
     *
     * @param userId unique user id
     * @return User if found, null otherwise
     */
    User getUserById(int userId);

    /**
     * Retrieves all system users.
     *
     * @return list of all users
     */
    List<User> getAllUsers();
}
