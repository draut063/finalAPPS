package com.appointment.schedule;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ResendOtpServlet")
public class ResendOtpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Get existing session, don't create new

        if (session == null || session.getAttribute("username") == null) {
            // No valid session or username found, redirect to login
            response.sendRedirect("index.html?error=" +
                    java.net.URLEncoder.encode("Session expired or invalid. Please log in.", "UTF-8"));
            return;
        }

        String username = (String) session.getAttribute("username"); // Get username from session

        // Generate a new OTP using the utility class
        String newOtp = GenerateAnadStoreOTP.generateOTP();

        // Store the new OTP and its new timestamp in the session (overwriting old ones)
        session.setAttribute("generatedOtp", newOtp);
        session.setAttribute("OtpTime", System.currentTimeMillis());

        // Optional: In a real app, send the OTP via email/SMS here
        // EmailService.sendOTP(username, newOtp); // Placeholder

        // Redirect back to the OTP page with a success message
        response.sendRedirect("Otp.jsp?message=" +
                java.net.URLEncoder.encode("A new OTP has been sent. Please check your email.", "UTF-8"));
    }

    // Allow POST requests to also be handled by doGet for simplicity
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}