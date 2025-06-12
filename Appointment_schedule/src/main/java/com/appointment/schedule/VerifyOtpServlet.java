package com.appointment.schedule;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/VerifyOtpServlet") // Maps this servlet to the /VerifyOtpServlet URL
public class VerifyOtpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Define the OTP validity period in milliseconds (5 minutes)
    // This must match the expected validity period for the OTP
    private static final long OTP_VALIDITY_PERIOD_MILLIS = 5 * 60 * 1000; // 5 minutes

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String enteredOtp = request.getParameter("otp");

        // Get existing session, DO NOT create a new one
        HttpSession session = request.getSession(false);

        // Essential security check: If no valid session or userId, redirect to login page.
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html?error=" +
                                   java.net.URLEncoder.encode("Session expired or invalid access. Please log in again.", "UTF-8"));
            return;
        }

        // Get the userId from the session. This is what we use to look up the OTP in the database.
        int userId = (int) session.getAttribute("userId");

        String storedOtp = null;
        long otpCreatedTimeMillis = 0; // Will store the creation timestamp from the DB

        try {
            // Fetch the active OTP and its creation time from the database using the utility method.
            // otpData will be {OTP_string, Created_DATE_milliseconds} based on your original GenerateAnadStoreOTP.
            String[] otpData = GenerateAnadStoreOTP.fetchActiveOTPFromDB(userId);

            // Check if an OTP was found in the database for this user.
            if (otpData == null || otpData[0] == null || otpData[1] == null) {
                // No active OTP found in the DB, or it was cleared/expired.
                response.sendRedirect("Otp.jsp?error=" +
                                       java.net.URLEncoder.encode("No active OTP found or it has already expired. Please request a new one.", "UTF-8"));
                return;
            }

            storedOtp = otpData[0]; // The OTP retrieved from the database.
            otpCreatedTimeMillis = Long.parseLong(otpData[1]); // The timestamp (in milliseconds) when the OTP was created.

            long currentTime = System.currentTimeMillis(); // Current time for expiration check.

            // Check if the OTP has expired based on the defined validity period (5 minutes).
            if (currentTime - otpCreatedTimeMillis > OTP_VALIDITY_PERIOD_MILLIS) {
                // OTP has expired. Deactivate it in the database.
                System.out.println("OTP for UserID " + userId + " has expired. Deactivating.");
                GenerateAnadStoreOTP.deactivateOTP(userId, storedOtp); // Deactivate in DB
                response.sendRedirect("Otp.jsp?error=" +
                                       java.net.URLEncoder.encode("OTP expired. Please request a new one.", "UTF-8"));
            }
            // If OTP is not expired, check if it matches the entered one
            else if (enteredOtp != null && enteredOtp.equals(storedOtp)) {
                // OTP is valid and within the time limit.
                // Deactivate the OTP in the database to prevent reuse.
                System.out.println("OTP for UserID " + userId + " verified successfully. Deactivating.");
                GenerateAnadStoreOTP.deactivateOTP(userId, storedOtp); // Deactivate in DB

                // Set a flag in session indicating that OTP validation was successful.
                session.setAttribute("otpValidated", true);

                // Redirect to the dashboard or the next intended page.
                response.sendRedirect("Schedule_an_appointment.jsp"); // Replace with your actual dashboard URL
            } else {
                // The entered OTP does not match the one stored in the database.
                System.out.println("Invalid OTP entered for UserID " + userId);
                response.sendRedirect("Otp.jsp?error=" +
                                       java.net.URLEncoder.encode("Invalid OTP. Please try again.", "UTF-8"));
            }
        } catch (SQLException e) {
            System.err.println("Database error during OTP verification for UserID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("Otp.jsp?error=" +
                                   java.net.URLEncoder.encode("A database error occurred during OTP verification. Please try again.", "UTF-F8"));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing timestamp from database during OTP verification: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("Otp.jsp?error=" +
                                   java.net.URLEncoder.encode("An internal error occurred. Please try again.", "UTF-8"));
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("An unexpected error occurred in VerifyOtpServlet for UserID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("Otp.jsp?error=" +
                                   java.net.URLEncoder.encode("An unexpected error occurred during OTP verification. Please contact support.", "UTF-8"));
        }
    }
}