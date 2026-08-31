package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.model.Bill;
import com.sunrisedental.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object implementation for Invoicing and Billing operations.
 * Demonstrates advanced database features (Stored Procedure execution via CallableStatement)
 * with robust fallback for distributed cloud engines.
 */
public class BillDAOImpl implements BillDAO {

    private static final Logger LOGGER = Logger.getLogger(BillDAOImpl.class.getName());

    @Override
    public boolean generateBill(Bill bill) {
        if (bill == null) {
            return false;
        }

        String sql = "INSERT INTO bills (appointment_number, consultation_fee, treatment_cost, " +
                     "total_amount, payment_status, issued_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getAppointmentNumber());
            ps.setDouble(2, bill.getConsultationFee());
            ps.setDouble(3, bill.getTreatmentCost());
            ps.setDouble(4, bill.getTotalAmount());
            ps.setString(5, bill.getPaymentStatus() != null ? bill.getPaymentStatus() : "PAID");

            LocalDateTime issuedAt = bill.getIssuedAt() != null ? bill.getIssuedAt() : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(issuedAt));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bill.setBillId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error generating bill for appointment: " + bill.getAppointmentNumber(), e);
        }
        return false;
    }

    @Override
    public Bill getBillByAppointment(String apptNumber) {
        if (apptNumber == null || apptNumber.trim().isEmpty()) {
            return null;
        }
        try {
            int number = Integer.parseInt(apptNumber.replaceAll("\\D", ""));
            return getBillByAppointment(number);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid appointment number format: " + apptNumber, e);
            return null;
        }
    }

    @Override
    public Bill getBillByAppointment(int apptNumber) {
        String sql = "SELECT bill_id, appointment_number, consultation_fee, treatment_cost, " +
                     "total_amount, payment_status, issued_at FROM bills WHERE appointment_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, apptNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving bill for appointment: " + apptNumber, e);
        }
        return null;
    }

    @Override
    public Bill getBillById(int billId) {
        String sql = "SELECT bill_id, appointment_number, consultation_fee, treatment_cost, " +
                     "total_amount, payment_status, issued_at FROM bills WHERE bill_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving bill by id: " + billId, e);
        }
        return null;
    }

    @Override
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT bill_id, appointment_number, consultation_fee, treatment_cost, " +
                     "total_amount, payment_status, issued_at FROM bills ORDER BY issued_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error retrieving all bills", e);
        }
        return list;
    }

    @Override
    public Bill calculateBillViaProcedure(String appointmentNumber, double consultationFee) {
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            return null;
        }
        int apptNum;
        try {
            apptNum = Integer.parseInt(appointmentNumber.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return null;
        }

        String callSql = "{CALL sp_CalculatePatientBill(?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall(callSql)) {

            cs.setInt(1, apptNum);
            cs.setDouble(2, consultationFee);
            cs.registerOutParameter(3, java.sql.Types.DECIMAL);
            cs.registerOutParameter(4, java.sql.Types.DECIMAL);

            cs.execute();

            double treatmentCost = cs.getDouble(3);
            double totalAmount = cs.getDouble(4);

            Bill bill = new Bill();
            bill.setAppointmentNumber(apptNum);
            bill.setConsultationFee(consultationFee);
            bill.setTreatmentCost(treatmentCost);
            bill.setTotalAmount(totalAmount);
            bill.setPaymentStatus("PAID");
            bill.setIssuedAt(LocalDateTime.now());
            return bill;
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, "Stored procedure invocation deferred to application calculation: " + e.getMessage());
            return calculateBillFallback(apptNum, consultationFee);
        }
    }

    private Bill calculateBillFallback(int apptNum, double consultationFee) {
        String sql = "SELECT t.standard_fee FROM appointments a " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE a.appointment_number = ?";
        double treatmentCost = 0.0;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, apptNum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    treatmentCost = rs.getDouble("standard_fee");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error resolving treatment cost for fallback billing", e);
        }

        Bill bill = new Bill();
        bill.setAppointmentNumber(apptNum);
        bill.setConsultationFee(consultationFee);
        bill.setTreatmentCost(treatmentCost);
        bill.setTotalAmount(treatmentCost + consultationFee);
        bill.setPaymentStatus("PAID");
        bill.setIssuedAt(LocalDateTime.now());
        return bill;
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setAppointmentNumber(rs.getInt("appointment_number"));
        bill.setConsultationFee(rs.getDouble("consultation_fee"));
        bill.setTreatmentCost(rs.getDouble("treatment_cost"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setPaymentStatus(rs.getString("payment_status"));

        Timestamp ts = rs.getTimestamp("issued_at");
        if (ts != null) {
            bill.setIssuedAt(ts.toLocalDateTime());
        }
        return bill;
    }
}
