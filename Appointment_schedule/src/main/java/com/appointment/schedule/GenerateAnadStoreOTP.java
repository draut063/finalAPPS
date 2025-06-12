package com.appointment.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Random;

public class GenerateAnadStoreOTP {

    // SQL queries as private static final strings
    private static final String SQL_INSERT_OTP = "INSERT INTO OTP_Records (UserID, Mail, OTP) VALUES (?, ?, ?)";
    private static final String SQL_DEACTIVATE_PREVIOUS_OTPS = "UPDATE OTP_Records SET IsActive = 0 WHERE UserID = ? AND IsActive = 1";
    // Modified SQL_FETCH_ACTIVE_OTP to also get IsActive status for more robust check
    private static final String SQL_FETCH_ACTIVE_OTP = "SELECT OTP, Created_DATE, IsActive FROM OTP_Records WHERE UserID = ? ORDER BY Created_DATE DESC";
    private static final String SQL_DEACTIVATE_OTP_ON_SUCCESS = "UPDATE OTP_Records SET IsActive = 0 WHERE UserID = ? AND OTP = ? AND IsActive = 1";

    // Define OTP validity period in milliseconds (5 minutes)
    private static final long OTP_VALIDITY_PERIOD_MILLIS = 5 * 60 * 1000; // 5 minutes * 60 seconds * 1000 milliseconds

    /**
     * Generates a random 6-digit One-Time Password (OTP).
     *
     * @return A String representing the 6-digit OTP.
     */
    public static String generateOTP() {
        Random random = new Random();
        int generatedOtp = 100000 + random.nextInt(900000); // Ensures a 6-digit number
        System.out.println("Generated OTP: " + generatedOtp); // For debugging/logging
        return String.valueOf(generatedOtp);
    }

    /**
     * Stores a new OTP record in the `OTP_Records` table for a given user.
     * Before inserting the new OTP, it deactivates any previously active OTPs for that user
     * to maintain the validity of only the most recent OTP.
     *
     * @param userId The `UserID` from the `AdminUsers` table.
     * @param email The user's email address, to be stored in the `Mail` column of `OTP_Records`.
     * @param otp The 6-digit OTP to be stored.
     * @return `true` if the OTP was successfully stored and previous ones deactivated, `false` otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public static boolean storeOTPInDB(int userId, String email, String otp) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtDeactivate = null;
        PreparedStatement pstmtInsert = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection(); // Obtain connection from your DBConnect utility
            conn.setAutoCommit(false); // Begin transaction for atomicity

            // Step 1: Deactivate any existing active OTPs for this UserID
            pstmtDeactivate = conn.prepareStatement(SQL_DEACTIVATE_PREVIOUS_OTPS);
            pstmtDeactivate.setInt(1, userId);
            pstmtDeactivate.executeUpdate();
            System.out.println("Previous OTPs for UserID " + userId + " deactivated.");

            // Step 2: Insert the new OTP record
            pstmtInsert = conn.prepareStatement(SQL_INSERT_OTP, Statement.RETURN_GENERATED_KEYS);
            pstmtInsert.setInt(1, userId);
            pstmtInsert.setString(2, email);
            pstmtInsert.setString(3, otp);

            int rowsAffected = pstmtInsert.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the auto-generated OTP_ID
                try (ResultSet keys = pstmtInsert.getGeneratedKeys()) {
                    if (keys.next()) {
                        int otpId = keys.getInt(1);
                        System.out.println("New OTP stored successfully with OTP_ID = " + otpId + " for UserID: " + userId + ", OTP: " + otp);
                    }
                }
                conn.commit(); // Commit the transaction if successful
                success = true;
            } else {
                conn.rollback(); // Rollback if the insert failed
                System.out.println("Failed to insert new OTP for UserID: " + userId);
            }

        } catch (SQLException e) {
            System.err.println("Database error during OTP storage for UserID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Attempt rollback on exception
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
            throw e; // Re-throw to be handled by the calling servlet
        } 
//        finally {
//            // Close resources safely in reverse order of creation
//            DBConnect.closeResources(null, pstmtDeactivate, null);
//            DBConnect.closeResources(conn, pstmtInsert, null); // conn is closed here
//        }
        return success;
    }

    /**
     * Fetches the *currently active* and *valid* OTP and its creation timestamp from the `OTP_Records` table for a given user.
     * This is used during the OTP verification process. An OTP is considered valid if it's active and
     * hasn't expired (i.e., created within the last `OTP_VALIDITY_PERIOD_MILLIS`).
     *
     * @param userId The `UserID` to fetch the OTP for.
     * @return A `String` array containing `{OTP, Created_DATE_Millis}` or `null` if no active and valid OTP is found.
     * @throws SQLException If a database access error occurs.
     */
    public static String[] fetchActiveOTPFromDB(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] otpData = null; // Will hold {OTP_string, Created_DATE_milliseconds}

        try {
            conn = DBConnect.getConnection();
            pstmt = conn.prepareStatement(SQL_FETCH_ACTIVE_OTP);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            // We fetch the most recently created OTP, regardless of its IsActive status initially.
            // Then we check both IsActive and its freshness.
            if (rs.next()) {
                String otp = rs.getString("OTP");
                Timestamp createdDate = rs.getTimestamp("Created_DATE");
                int isActive = rs.getInt("IsActive"); // Get the IsActive status

                // Get current time
                long currentTimeMillis = System.currentTimeMillis();

                // Check if OTP is active AND within the validity period
                if (isActive == 1 && createdDate != null &&
                    (currentTimeMillis - createdDate.getTime() <= OTP_VALIDITY_PERIOD_MILLIS)) {
                    otpData = new String[2];
                    otpData[0] = otp;
                    otpData[1] = String.valueOf(createdDate.getTime()); // Convert Timestamp to milliseconds
                    System.out.println("Active and valid OTP found for UserID " + userId + ": " + otp);
                } else {
                    System.out.println("No active and valid OTP found for UserID " + userId +
                                       ". OTP found was inactive or expired.");
                }
            } else {
                System.out.println("No OTP records found for UserID " + userId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Database error while fetching active OTP for UserID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception
        } finally {
            // Close resources safely
           // DBConnect.closeResources(conn, pstmt, rs);
        }
        return otpData;
    }

    /**
     * Deactivates a specific OTP record in the `OTP_Records` table after successful verification.
     * This marks the OTP as used and prevents its reuse.
     *
     * @param userId The `UserID` associated with the OTP.
     * @param otp The OTP string that was successfully verified.
     * @return `true` if the OTP was deactivated, `false` otherwise (e.g., OTP not found or already inactive).
     * @throws SQLException If a database access error occurs.
     */
    public static boolean deactivateOTP(int userId, String otp) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnect.getConnection();
            pstmt = conn.prepareStatement(SQL_DEACTIVATE_OTP_ON_SUCCESS);
            pstmt.setInt(1, userId);
            pstmt.setString(2, otp);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("OTP successfully deactivated in OTP_Records for UserID: " + userId);
                success = true;
            } else {
                System.out.println("Failed to deactivate OTP for UserID: " + userId + " (OTP not found or already inactive)");
            }
        } catch (SQLException e) {
            System.err.println("Database error while deactivating OTP for UserID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception
        } finally {
            // Close resources safely
           // DBConnect.closeResources(conn, pstmt, null);
        }
        return success;
    }
}