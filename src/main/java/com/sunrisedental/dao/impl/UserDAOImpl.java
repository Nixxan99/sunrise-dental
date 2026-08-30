package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;
import com.sunrisedental.util.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object implementation for User management using PreparedStatement.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public User authenticate(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (verifyPassword(password, storedHash)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error authenticating user: " + username, e);
        }
        return null;
    }

    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error inserting user: " + user.getUsername(), e);
        }
        return false;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error finding user by username: " + username, e);
        }
        return null;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error finding user by id: " + userId, e);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users ORDER BY full_name";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving all users", e);
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        return user;
    }

    /**
     * Verifies password against stored password using SHA-256 or direct comparison.
     */
    public static boolean verifyPassword(String plainPassword, String storedPasswordOrHash) {
        if (plainPassword == null || storedPasswordOrHash == null) {
            return false;
        }
        if (plainPassword.equals(storedPasswordOrHash)) {
            return true;
        }
        String hashed = hashPassword(plainPassword);
        return hashed.equalsIgnoreCase(storedPasswordOrHash);
    }

    /**
     * Generates SHA-256 hash representation of a password string.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing", e);
        }
    }
}
