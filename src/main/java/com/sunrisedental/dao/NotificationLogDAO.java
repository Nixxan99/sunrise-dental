package com.sunrisedental.dao;

/**
 * DAO interface for logging and auditing patient notification alerts (CIS6003 Task B).
 */
public interface NotificationLogDAO {

    /**
     * Persists an audit log entry for a dispatched notification.
     *
     * @param recipientPhone patient recipient contact
     * @param messageBody text payload delivered
     * @param status dispatch status (e.g. "SENT", "DELIVERED", "SIMULATED")
     * @return true if logged successfully, false otherwise
     */
    boolean logNotification(String recipientPhone, String messageBody, String status);
}
