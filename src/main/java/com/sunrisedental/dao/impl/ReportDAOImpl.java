package com.sunrisedental.dao.impl;

import com.sunrisedental.dao.ReportDAO;
import com.sunrisedental.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ReportDAO implementation aggregating clinic statistics from Cloud TiDB.
 */
public class ReportDAOImpl implements ReportDAO {

    private static final Logger LOGGER = Logger.getLogger(ReportDAOImpl.class.getName());

    @Override
    public List<TreatmentReportItem> getTreatmentBreakdown() {
        List<TreatmentReportItem> list = new ArrayList<>();
        String sql = "SELECT t.treatment_name, "
                + "       COUNT(a.appointment_number) AS total_count, "
                + "       COALESCE(SUM(b.total_amount), SUM(t.standard_fee + 1500), 0) AS total_revenue, "
                + "       AVG(t.standard_fee) AS avg_fee "
                + "FROM treatments t "
                + "LEFT JOIN appointments a ON t.treatment_id = a.treatment_id "
                + "LEFT JOIN bills b ON a.appointment_number = b.appointment_number "
                + "GROUP BY t.treatment_id, t.treatment_name "
                + "ORDER BY total_revenue DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TreatmentReportItem(
                        rs.getString("treatment_name"),
                        rs.getInt("total_count"),
                        rs.getDouble("total_revenue"),
                        rs.getDouble("avg_fee")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error aggregating treatment report breakdown", e);
        }
        return list;
    }

    @Override
    public List<DoctorReportItem> getDoctorBreakdown() {
        List<DoctorReportItem> list = new ArrayList<>();
        String sql = "SELECT d.name AS doctor_name, "
                + "       d.specialization, "
                + "       COUNT(a.appointment_number) AS total_count, "
                + "       COALESCE(SUM(b.total_amount), 0) AS total_revenue "
                + "FROM dentists d "
                + "LEFT JOIN appointments a ON d.dentist_id = a.dentist_id "
                + "LEFT JOIN bills b ON a.appointment_number = b.appointment_number "
                + "GROUP BY d.dentist_id, d.name, d.specialization "
                + "ORDER BY total_count DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DoctorReportItem(
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getInt("total_count"),
                        rs.getDouble("total_revenue")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error aggregating doctor breakdown report", e);
        }
        return list;
    }

    @Override
    public Map<String, Object> getOverallSummary() {
        Map<String, Object> map = new HashMap<>();
        String sql = "SELECT "
                + "  (SELECT COUNT(*) FROM appointments) AS total_appointments, "
                + "  (SELECT COUNT(*) FROM appointments WHERE status = 'SCHEDULED') AS scheduled_appointments, "
                + "  (SELECT COUNT(*) FROM appointments WHERE status = 'COMPLETED') AS completed_appointments, "
                + "  (SELECT COUNT(*) FROM patients) AS total_patients, "
                + "  (SELECT COUNT(*) FROM dentists) AS total_dentists, "
                + "  (SELECT COALESCE(SUM(total_amount), 0) FROM bills) AS total_revenue, "
                + "  (SELECT COALESCE(AVG(total_amount), 0) FROM bills) AS avg_invoice_value";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                map.put("totalAppointments", rs.getInt("total_appointments"));
                map.put("scheduledAppointments", rs.getInt("scheduled_appointments"));
                map.put("completedAppointments", rs.getInt("completed_appointments"));
                map.put("totalPatients", rs.getInt("total_patients"));
                map.put("totalDentists", rs.getInt("total_dentists"));
                map.put("totalRevenue", rs.getDouble("total_revenue"));
                map.put("avgInvoiceValue", rs.getDouble("avg_invoice_value"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching overall report summary", e);
        }
        return map;
    }
}
