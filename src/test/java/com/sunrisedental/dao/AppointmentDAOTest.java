package com.sunrisedental.dao;

import com.sunrisedental.dao.impl.AppointmentDAOImpl;
import com.sunrisedental.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AppointmentDAO Universal Search and Operations Tests")
class AppointmentDAOTest {

    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() {
        appointmentDAO = new AppointmentDAOImpl();
    }

    @Test
    @DisplayName("Should retrieve appointments list")
    void shouldGetAllAppointments() {
        List<Appointment> list = appointmentDAO.getAllAppointments();
        assertNotNull(list);
    }

    @Test
    @DisplayName("Should perform universal search on appointments")
    void shouldSearchAppointmentsUniversally() {
        List<Appointment> all = appointmentDAO.getAllAppointments();
        if (!all.isEmpty()) {
            Appointment first = all.get(0);
            // Search by appointment number
            List<Appointment> byNum = appointmentDAO.searchAppointmentsUniversal(String.valueOf(first.getAppointmentNumber()));
            assertNotNull(byNum);
            assertFalse(byNum.isEmpty());

            // Search by patient name snippet if available
            if (first.getPatientName() != null && !first.getPatientName().isEmpty()) {
                String term = first.getPatientName().split(" ")[0];
                List<Appointment> byName = appointmentDAO.searchAppointmentsUniversal(term);
                assertNotNull(byName);
                assertFalse(byName.isEmpty());
            }
        }

        // Search with empty or non-matching term
        List<Appointment> empty = appointmentDAO.searchAppointmentsUniversal("NON_EXISTING_XYZ_9999");
        assertNotNull(empty);
        assertTrue(empty.isEmpty());
    }
}
