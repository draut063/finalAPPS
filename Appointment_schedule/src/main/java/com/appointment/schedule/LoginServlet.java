package com.appointment.schedule;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/LoginServlet") 
public class LoginServlet extends HttpServlet {
     private static final long serialVersionUID = 1L;

    // SQL query for user login
    private static final String SQL_LOGIN = "SELECT UserID, IsAdmin, IsActive, Mail FROM AdminUsers WHERE Username = ? AND Password_AU = ?";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("Username");
        String password = request.getParameter("Password_AU");

        try {
            // Use try-with-resources for Connection and PreparedStatement to ensure they are closed
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement psLogin = conn.prepareStatement(SQL_LOGIN)) {

                psLogin.setString(1, username);
                psLogin.setString(2, password);

                try (ResultSet rs = psLogin.executeQuery()) {
                    if (rs.next()) {
                        // User found, retrieve details
                        int userId = rs.getInt("UserID");
                        // Assuming IsAdmin column stores "Admin", "User", or "Reception"
                        String role = rs.getString("IsAdmin");
                        // Assuming 1 for active, 0 for inactive
                        int isActive = rs.getInt("IsActive");
                        String userMail = rs.getString("Mail");

                        if (isActive != 1) {
                            // Account is inactive
                            response.sendRedirect("index.html?error=" +
                                        java.net.URLEncoder.encode("Account is inactive. Please contact support.", "UTF-8"));
                            return;      
                        } 

                        // Role-based redirection
                        if ("Admin".equalsIgnoreCase(role)) {
                            // For Admins, no OTP required, redirect directly to Dashboard
                            response.sendRedirect("DashBoard.html");
                        } else if ("User".equalsIgnoreCase(role)) {
                            // For regular Users, generate and send OTP
                            String otp = GenerateAnadStoreOTP.generateOTP();

                            // Store OTP in the database
                            boolean otpStored = false;
                            try {
                                otpStored = GenerateAnadStoreOTP.storeOTPInDB(userId, userMail, otp);
                            } catch (SQLException dbEx) {
                                System.err.println("Database error during OTP storage for user " + userId + ": " + dbEx.getMessage());
                                dbEx.printStackTrace();
                                response.sendRedirect("index.html?error=" +
                                            java.net.URLEncoder.encode("A database error occurred during OTP generation. Please try again.", "UTF-8"));
                                return;
                            }

                            if (otpStored) {
                                // Attempt to send the OTP email using the centralized EmailService
                                try {
                                    // *** THIS IS THE CRUCIAL LINE TO UNCOMMENT AND USE ***
                                    EmailServiceOTP.sendOtpEmail(userMail, otp);

                                    // Store necessary details in session for OTP verification
                                    HttpSession session = request.getSession();
                                    session.setAttribute("userId", userId);
                                    session.setAttribute("username", username);
                                    session.setAttribute("userEmail", userMail); // Useful for "Resend OTP" functionality on Otp.jsp

                                    response.sendRedirect("Otp.jsp"); // Redirect to OTP verification page
                                }
                                catch (Exception mailEx) { // Catch generic Exception for email sending issues
                                    System.err.println("Error sending OTP email to " + userMail + ": " + mailEx.getMessage());
                                    mailEx.printStackTrace();
                                    // Even if email sending fails, the OTP is stored.
                                    // Redirect to Otp.jsp, but pass an error message about email.
                                    HttpSession session = request.getSession();
                                    session.setAttribute("userId", userId);
                                    session.setAttribute("username", username);
                                    session.setAttribute("userEmail", userMail);
                                    // You can add a session attribute to inform Otp.jsp about the email send failure
                                    session.setAttribute("otpEmailError", "Failed to send OTP email. Please check your email address or try resending.");
                                    response.sendRedirect("Otp.jsp?emailError=" +
                                            java.net.URLEncoder.encode("Failed to send OTP email. You can still verify using the OTP provided.", "UTF-8"));
                                }
                            } else {
                                // This block handles if GenerateAnadStoreOTP.storeOTPInDB returned false
                                response.sendRedirect("index.html?error=" +
                                            java.net.URLEncoder.encode("Failed to generate or store OTP. Please try again.", "UTF-8"));
                            }
                        } else if ("Security".equalsIgnoreCase(role)) {
                            // For Reception, redirect directly to their specific page
                            response.sendRedirect("SecurityPage.jsp"); // Redirect to your reception page
                        } else {
                            // Handle cases where role is neither "Admin", "User", nor "Reception"
                            response.sendRedirect("index.html?error=" +
                                        java.net.URLEncoder.encode("Unknown user role. Please contact support.", "UTF-8"));
                        }

                    } else {
                        // Invalid username or password
                        response.sendRedirect("index.html?error=" +
                                java.net.URLEncoder.encode("Invalid username or password.", "UTF-8"));
                    }
                } // try-with-resources for ResultSet
            } // try-with-resources for Connection and PreparedStatement
        } catch (SQLException e) {
            System.err.println("Database error during login process: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("index.html?error=" +
                    java.net.URLEncoder.encode("A database error occurred. Please try again later.", "UTF-8"));
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("An unexpected error occurred in LoginServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("index.html?error=" +
                    java.net.URLEncoder.encode("An unexpected error occurred. Please try again.", "UTF-8"));
        }
    }
}