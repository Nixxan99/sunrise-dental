package com.sunrisedental.controller;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.dao.impl.AppointmentDAOImpl;
import com.sunrisedental.dao.impl.BillDAOImpl;
import com.sunrisedental.dao.impl.DentistDAOImpl;
import com.sunrisedental.dao.impl.TreatmentDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller aggregating dashboard metrics, today's appointments, and recent bookings.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private AppointmentDAO appointmentDAO;
    private DentistDAO dentistDAO;
    private TreatmentDAO treatmentDAO;
    private BillDAO billDAO;

    @Override
    public void init() {
        this.appointmentDAO = new AppointmentDAOImpl();
        this.dentistDAO = new DentistDAOImpl();
        this.treatmentDAO = new TreatmentDAOImpl();
        this.billDAO = new BillDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        List<Bill> bills = billDAO.getAllBills();

        int totalAppointments = appointments.size();
        long scheduledCount = appointments.stream()
                .filter(a -> "SCHEDULED".equalsIgnoreCase(a.getStatus()))
                .count();
        long completedCount = appointments.stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .count();

        LocalDate today = LocalDate.now();
        long todayCount = appointments.stream()
                .filter(a -> today.equals(a.getAppointmentDate()))
                .count();

        double totalRevenue = bills.stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();

        int totalDentists = dentistDAO.getAllDentists().size();
        int totalTreatments = treatmentDAO.getAllTreatments().size();

        request.setAttribute("appointments", appointments);
        request.setAttribute("totalAppointments", totalAppointments);
        request.setAttribute("scheduledCount", scheduledCount);
        request.setAttribute("completedCount", completedCount);
        request.setAttribute("todayCount", todayCount);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalDentists", totalDentists);
        request.setAttribute("totalTreatments", totalTreatments);

        request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
    }
}
