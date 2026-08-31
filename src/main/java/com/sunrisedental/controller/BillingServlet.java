package com.sunrisedental.controller;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dao.impl.AppointmentDAOImpl;
import com.sunrisedental.dao.impl.BillDAOImpl;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.service.BillingService;
import com.sunrisedental.service.GeminiCarePlanService;
import com.sunrisedental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller handling dental invoice generation, fee calculation, stored procedure billing,
 * receipt rendering, and Google Gemini AI personalized post-care plan generation.
 */
@WebServlet(name = "BillingServlet", urlPatterns = {"/billing"})
public class BillingServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(BillingServlet.class.getName());

    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private BillingService billingService;
    private GeminiCarePlanService geminiCarePlanService;

    @Override
    public void init() {
        this.appointmentDAO = new AppointmentDAOImpl();
        this.billDAO = new BillDAOImpl();
        this.billingService = new BillingService();
        this.geminiCarePlanService = new GeminiCarePlanService();
    }

    // Setters for unit testing and DI
    public void setAppointmentDAO(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public void setBillDAO(BillDAO billDAO) {
        this.billDAO = billDAO;
    }

    public void setBillingService(BillingService billingService) {
        this.billingService = billingService;
    }

    public void setGeminiCarePlanService(GeminiCarePlanService geminiCarePlanService) {
        this.geminiCarePlanService = geminiCarePlanService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String apptNumberStr = request.getParameter("appointmentNumber");
        if (apptNumberStr == null || apptNumberStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            int apptNumber = Integer.parseInt(apptNumberStr.replaceAll("\\D", ""));
            Appointment appointment = appointmentDAO.getAppointmentByNumber(apptNumber);

            if (appointment == null) {
                request.setAttribute("errorMessage", "Appointment #" + apptNumber + " could not be found.");
                request.getRequestDispatcher("/views/appointment-view.jsp").forward(request, response);
                return;
            }

            // Fetch Gemini AI Personalized Care Plan
            String geminiAdvice = geminiCarePlanService.generatePostTreatmentAdvice(
                    appointment.getTreatmentName(),
                    appointment.getPatientName()
            );
            request.setAttribute("geminiAdvice", geminiAdvice);

            Bill bill = billDAO.getBillByAppointment(apptNumber);
            if (bill != null) {
                String receiptText = billingService.generateReceipt(bill, appointment);
                request.setAttribute("bill", bill);
                request.setAttribute("appointment", appointment);
                request.setAttribute("receiptText", receiptText);
                request.getRequestDispatcher("/views/bill-receipt.jsp").forward(request, response);
            } else {
                // Prepare default billing figures for preview
                double treatmentCost = appointment.getCost() > 0 ? appointment.getCost() : 3000.00;
                double defaultConsultationFee = 1500.00;
                double total = billingService.calculateTotalCost(treatmentCost, defaultConsultationFee);

                Bill draftBill = new Bill();
                draftBill.setAppointmentNumber(apptNumber);
                draftBill.setConsultationFee(defaultConsultationFee);
                draftBill.setTreatmentCost(treatmentCost);
                draftBill.setTotalAmount(total);
                draftBill.setPaymentStatus("PAID");

                request.setAttribute("bill", draftBill);
                request.setAttribute("appointment", appointment);
                request.setAttribute("isDraft", true);
                request.getRequestDispatcher("/views/bill-receipt.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid appointment number format: " + apptNumberStr, e);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String apptNumberStr = request.getParameter("appointmentNumber");
        String consultationFeeStr = request.getParameter("consultationFee");
        String treatmentCostStr = request.getParameter("treatmentCost");
        String paymentStatus = request.getParameter("paymentStatus");

        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            paymentStatus = "PAID";
        }

        try {
            int apptNumber = Integer.parseInt(apptNumberStr.replaceAll("\\D", ""));
            double consultationFee = Double.parseDouble(consultationFeeStr.trim());
            double treatmentCost = Double.parseDouble(treatmentCostStr.trim());

            if (!ValidationUtil.isNonNegative(consultationFee) || !ValidationUtil.isNonNegative(treatmentCost)) {
                request.setAttribute("errorMessage", "Fees cannot be negative.");
                forwardToReceiptWithData(request, response, apptNumber, consultationFee, treatmentCost, paymentStatus);
                return;
            }

            Appointment appointment = appointmentDAO.getAppointmentByNumber(apptNumber);
            if (appointment == null) {
                request.setAttribute("errorMessage", "Cannot generate bill: Appointment not found.");
                request.getRequestDispatcher("/views/appointment-view.jsp").forward(request, response);
                return;
            }

            // Check if existing bill is present
            Bill bill = billDAO.getBillByAppointment(apptNumber);
            if (bill == null) {
                bill = billingService.createBill(apptNumber, consultationFee, treatmentCost, paymentStatus);
                boolean saved = billDAO.generateBill(bill);
                if (!saved) {
                    request.setAttribute("errorMessage", "Failed to save bill to database. Please retry.");
                    forwardToReceiptWithData(request, response, apptNumber, consultationFee, treatmentCost, paymentStatus);
                    return;
                }
                // Update appointment status to COMPLETED
                appointmentDAO.updateStatus(apptNumber, "COMPLETED");
                appointment.setStatus("COMPLETED");
            }

            // Fetch Gemini AI Personalized Care Plan
            String geminiAdvice = geminiCarePlanService.generatePostTreatmentAdvice(
                    appointment.getTreatmentName(),
                    appointment.getPatientName()
            );
            request.setAttribute("geminiAdvice", geminiAdvice);

            String receiptText = billingService.generateReceipt(bill, appointment);
            request.setAttribute("bill", bill);
            request.setAttribute("appointment", appointment);
            request.setAttribute("receiptText", receiptText);
            request.setAttribute("successMessage", "Invoice successfully generated and processed.");

            request.getRequestDispatcher("/views/bill-receipt.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation failed during bill generation", e);
            request.setAttribute("errorMessage", "Validation error: " + e.getMessage());
            request.getRequestDispatcher("/views/appointment-view.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error generating bill", e);
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            request.getRequestDispatcher("/views/appointment-view.jsp").forward(request, response);
        }
    }

    private void forwardToReceiptWithData(HttpServletRequest request, HttpServletResponse response,
                                          int apptNumber, double consultationFee, double treatmentCost,
                                          String paymentStatus) throws ServletException, IOException {
        Appointment appointment = appointmentDAO.getAppointmentByNumber(apptNumber);
        Bill draftBill = new Bill();
        draftBill.setAppointmentNumber(apptNumber);
        draftBill.setConsultationFee(consultationFee);
        draftBill.setTreatmentCost(treatmentCost);
        draftBill.setTotalAmount(consultationFee + treatmentCost);
        draftBill.setPaymentStatus(paymentStatus);

        String geminiAdvice = geminiCarePlanService.generatePostTreatmentAdvice(
                appointment != null ? appointment.getTreatmentName() : "Dental Care",
                appointment != null ? appointment.getPatientName() : "Patient"
        );
        request.setAttribute("geminiAdvice", geminiAdvice);

        request.setAttribute("bill", draftBill);
        request.setAttribute("appointment", appointment);
        request.setAttribute("isDraft", true);
        request.getRequestDispatcher("/views/bill-receipt.jsp").forward(request, response);
    }
}
