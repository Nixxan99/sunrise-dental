package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.util.DBConnection;

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
 * Data Access Object implementation for Dentist directory operations.
 */
public class DentistDAOImpl implements DentistDAO {

    private static final Logger LOGGER = Logger.getLogger(DentistDAOImpl.class.getName());

    @Override
    public List<Dentist> getAllDentists() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT dentist_id, name, specialization, contact_number FROM dentists ORDER BY name";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDentist(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving all dentists", e);
        }
        return list;
    }

    @Override
    public Dentist getDentistById(int dentistId) {
        String sql = "SELECT dentist_id, name, specialization, contact_number FROM dentists WHERE dentist_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDentist(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving dentist by id: " + dentistId, e);
        }
        return null;
    }

    @Override
    public boolean addDentist(Dentist dentist) {
        if (dentist == null) {
            return false;
        }
        String sql = "INSERT INTO dentists (name, specialization, contact_number) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dentist.getName());
            ps.setString(2, dentist.getSpecialization());
            ps.setString(3, dentist.getContactNumber());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        dentist.setDentistId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error inserting dentist: " + dentist.getName(), e);
        }
        return false;
    }

    @Override
    public boolean createDentist(Dentist dentist) {
        return addDentist(dentist);
    }

    @Override
    public boolean updateDentist(Dentist dentist) {
        if (dentist == null || dentist.getDentistId() <= 0) {
            return false;
        }
        String sql = "UPDATE dentists SET name = ?, specialization = ?, contact_number = ? WHERE dentist_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dentist.getName());
            ps.setString(2, dentist.getSpecialization());
            ps.setString(3, dentist.getContactNumber());
            ps.setInt(4, dentist.getDentistId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error updating dentist id: " + dentist.getDentistId(), e);
        }
        return false;
    }

    @Override
    public boolean deleteDentist(int dentistId) {
        String sql = "DELETE FROM dentists WHERE dentist_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dentistId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error deleting dentist id: " + dentistId, e);
        }
        return false;
    }

    private Dentist mapResultSetToDentist(ResultSet rs) throws SQLException {
        Dentist dentist = new Dentist();
        dentist.setDentistId(rs.getInt("dentist_id"));
        dentist.setName(rs.getString("name"));
        dentist.setSpecialization(rs.getString("specialization"));
        dentist.setContactNumber(rs.getString("contact_number"));
        return dentist;
    }
}
