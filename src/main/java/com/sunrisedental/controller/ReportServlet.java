package com.sunrisedental.controller;

import com.google.gson.Gson;
import com.sunrisedental.dao.ReportDAO;
import com.sunrisedental.dao.impl.ReportDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller providing Clinic Analytics, Decision-Making Reports, and Chart Visualizations.
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {

    private ReportDAO reportDAO;
    private Gson gson;

    @Override
    public void init() {
        this.reportDAO = new ReportDAOImpl();
        this.gson = new Gson();
    }

    // Setter for testing / DI
    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ReportDAO.TreatmentReportItem> treatmentReports = reportDAO.getTreatmentBreakdown();
        List<ReportDAO.DoctorReportItem> doctorReports = reportDAO.getDoctorBreakdown();
        Map<String, Object> summary = reportDAO.getOverallSummary();

        // Chart 1: Treatment Revenue Breakdown
        List<String> treatmentLabels = new ArrayList<>();
        List<Double> treatmentRevenues = new ArrayList<>();
        List<Integer> treatmentCounts = new ArrayList<>();

        for (ReportDAO.TreatmentReportItem item : treatmentReports) {
            treatmentLabels.add(item.getTreatmentName());
            treatmentRevenues.add(item.getTotalRevenue());
            treatmentCounts.add(item.getAppointmentCount());
        }

        // Chart 2: Doctor Workload Breakdown
        List<String> doctorLabels = new ArrayList<>();
        List<Integer> doctorCounts = new ArrayList<>();
        List<Double> doctorRevenues = new ArrayList<>();

        for (ReportDAO.DoctorReportItem item : doctorReports) {
            doctorLabels.add(item.getDoctorName());
            doctorCounts.add(item.getAppointmentCount());
            doctorRevenues.add(item.getTotalRevenue());
        }

        request.setAttribute("treatmentReports", treatmentReports);
        request.setAttribute("doctorReports", doctorReports);
        request.setAttribute("summary", summary);

        // Pass JSON strings for Chart.js
        request.setAttribute("treatmentLabelsJson", gson.toJson(treatmentLabels));
        request.setAttribute("treatmentRevenuesJson", gson.toJson(treatmentRevenues));
        request.setAttribute("treatmentCountsJson", gson.toJson(treatmentCounts));
        request.setAttribute("doctorLabelsJson", gson.toJson(doctorLabels));
        request.setAttribute("doctorCountsJson", gson.toJson(doctorCounts));
        request.setAttribute("doctorRevenuesJson", gson.toJson(doctorRevenues));

        request.getRequestDispatcher("/views/reports.jsp").forward(request, response);
    }
}
