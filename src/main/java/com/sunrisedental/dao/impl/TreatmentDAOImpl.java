package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;
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
 * Data Access Object implementation for Treatment catalog operations.
 */
public class TreatmentDAOImpl implements TreatmentDAO {

    private static final Logger LOGGER = Logger.getLogger(TreatmentDAOImpl.class.getName());

    @Override
    public List<Treatment> getAllTreatments() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT treatment_id, treatment_name, standard_fee FROM treatments ORDER BY treatment_name";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving all treatments", e);
        }
        return list;
    }

    @Override
    public Treatment getTreatmentById(int id) {
        String sql = "SELECT treatment_id, treatment_name, standard_fee FROM treatments WHERE treatment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTreatment(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving treatment by id: " + id, e);
        }
        return null;
    }

    @Override
    public boolean addTreatment(Treatment treatment) {
        if (treatment == null) {
            return false;
        }
        String sql = "INSERT INTO treatments (treatment_name, standard_fee) VALUES (?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, treatment.getTreatmentName());
            ps.setDouble(2, treatment.getStandardFee());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        treatment.setTreatmentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error inserting treatment: " + treatment.getTreatmentName(), e);
        }
        return false;
    }

    @Override
    public boolean createTreatment(Treatment treatment) {
        return addTreatment(treatment);
    }

    @Override
    public boolean updateTreatment(Treatment treatment) {
        if (treatment == null || treatment.getTreatmentId() <= 0) {
            return false;
        }
        String sql = "UPDATE treatments SET treatment_name = ?, standard_fee = ? WHERE treatment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, treatment.getTreatmentName());
            ps.setDouble(2, treatment.getStandardFee());
            ps.setInt(3, treatment.getTreatmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error updating treatment id: " + treatment.getTreatmentId(), e);
        }
        return false;
    }

    @Override
    public boolean deleteTreatment(int id) {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error deleting treatment id: " + id, e);
        }
        return false;
    }

    private Treatment mapResultSetToTreatment(ResultSet rs) throws SQLException {
        Treatment treatment = new Treatment();
        treatment.setTreatmentId(rs.getInt("treatment_id"));
        treatment.setTreatmentName(rs.getString("treatment_name"));
        treatment.setStandardFee(rs.getDouble("standard_fee"));
        return treatment;
    }
}
