package com.appointment.schedule; // Adjust package as necessary

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LogoutServlet") // Ensure this matches the href in your JSPs
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Do NOT create a new session if one doesn't exist

        if (session != null) {
            session.invalidate(); // Invalidate the session
            System.out.println("User session invalidated successfully.");
        }

        // Set Cache-Control headers for the logout response itself
        // This helps prevent issues where the logout page might be cached
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // Redirect the user to the login page (index.html) with a logout success parameter
        response.sendRedirect(request.getContextPath() + "/index.html?logout=true");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Typically logout is handled by GET requests from navigation links,
        // but it's good practice to handle POST if a form submits to it.
        doGet(request, response);
    }
}