package com.sunrisedental.dao;

import com.sunrisedental.model.Bill;
import java.util.List;

/**
 * Data Access Object interface for Invoicing and Billing operations.
 */
public interface BillDAO {

    /**
     * Persists a newly generated bill.
     *
     * @param bill the bill to save
     * @return true if successfully saved, false otherwise
     */
    boolean generateBill(Bill bill);

    /**
     * Retrieves the bill associated with a specific appointment number (as string).
     *
     * @param apptNumber appointment number
     * @return Bill if found, null otherwise
     */
    Bill getBillByAppointment(String apptNumber);

    /**
     * Retrieves the bill associated with a specific appointment number (as integer).
     *
     * @param apptNumber appointment number
     * @return Bill if found, null otherwise
     */
    Bill getBillByAppointment(int apptNumber);

    /**
     * Retrieves a bill by its primary key.
     *
     * @param billId unique bill identifier
     * @return Bill if found, null otherwise
     */
    Bill getBillById(int billId);

    /**
     * Retrieves all issued bills.
     *
     * @return list of bills
     */
    List<Bill> getAllBills();

    /**
     * Demonstrates advanced database features by executing a stored procedure
     * ({CALL sp_CalculatePatientBill(?, ?, ?, ?)}) via JDBC CallableStatement.
     *
     * @param appointmentNumber unique appointment number
     * @param consultationFee doctor's consultation fee
     * @return calculated Bill entity
     */
    Bill calculateBillViaProcedure(String appointmentNumber, double consultationFee);
}
