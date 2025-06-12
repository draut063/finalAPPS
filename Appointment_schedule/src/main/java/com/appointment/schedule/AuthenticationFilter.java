package com.appointment.schedule; // Your package name

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthenticationFilter implements Filter {

    // You can initialize resources here if needed
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the requested URI (e.g., /scheduleAppointment.jsp, /LogoutServlet)
        String requestURI = httpRequest.getRequestURI();

        // Define pages that DO NOT require authentication (e.g., login, CSS, JS, images, OTP)
        // Adjust these paths based on your project structure
        boolean isPublicResource = requestURI.endsWith("/index.html") ||
                                   requestURI.endsWith("/LoginServlet") ||
                                   requestURI.endsWith("/otp-verification.jsp") ||
                                   requestURI.endsWith("/VerifyOtpServlet") ||
                                   requestURI.endsWith("/ResendOtpServlet") ||
                                   requestURI.endsWith(".css") ||
                                   requestURI.endsWith(".js") ||
                                   requestURI.endsWith(".png") ||
                                   requestURI.endsWith(".jpg"); // Add other static resources

        // Get the session (don't create a new one if it doesn't exist)
        HttpSession session = httpRequest.getSession(false);

        // Check if the user is logged in (by checking a session attribute)
        // Replace "username" with the actual session attribute you set upon successful login.
        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);

        // If it's a public resource OR the user is logged in, proceed
        if (isPublicResource || isLoggedIn) {
            // Important: Set cache control headers for protected pages
            // This is primarily for the JSPs like scheduleAppointment.jsp, viewAppointments.jsp
            // It helps prevent browser caching of sensitive content.
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
            httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
            httpResponse.setHeader("Expires", "0"); // Proxies.

            chain.doFilter(request, response); // Allow the request to proceed to the target resource (JSP/Servlet)
        } else {
            // If it's a protected resource AND the user is NOT logged in, redirect to login page
            System.out.println("Unauthorized access attempt to: " + requestURI);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.html?error=UnauthorizedAccess");
        }
    }

    // Clean up resources if needed
    @Override
    public void destroy() {
        System.out.println("AuthenticationFilter destroyed.");
    }
}