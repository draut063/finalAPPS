package com.appointment.schedule; // Ensure this matches your package structure

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // For session messages

@WebServlet("/UpdateAppointmentServlet") // This annotation maps the servlet to the URL /UpdateAppointmentServlet
public class UpdateAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(); // Get the session to set success/error messages

        // 1. Retrieve all form parameters from the POST request
        // The 'id' parameter is hidden in the form but crucial for the WHERE clause
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String location = request.getParameter("location");
        String address = request.getParameter("address");
        String meetingType = request.getParameter("meeting_type");
        String priority = request.getParameter("priority");
        String requiredAttendee = request.getParameter("required_attendee");
        String appointmentDate = request.getParameter("appointment_date"); // Comes in yyyy-MM-dd format
        String fromTime = request.getParameter("from_time");             // Comes in HH:mm format
        String toTime = request.getParameter("to_time");                 // Comes in HH:mm format
        String meetingAgenda = request.getParameter("meeting_agenda");

        Connection conn = null;
        PreparedStatement pstmt = null;
        int id = 0; // Initialize id to a default value

        try {
            // Validate the ID. If it's not a valid integer, this will throw a NumberFormatException.
            id = Integer.parseInt(idStr);

            // Get a database connection from your DBConnect utility
            conn = DBConnect.getConnection();

            // SQL UPDATE query: Update all relevant columns based on the 'id'
            // The 'id' itself is used in the WHERE clause, not in the SET clause.
            String sql = "UPDATE Schedule_an_Appointment SET " +
                         "name = ?, email = ?, phone = ?, location = ?, address = ?, " +
                         "meeting_type = ?, priority = ?, Required_Attendee = ?, " +
                         "appointment_date = ?, from_time = ?, to_time = ?, Meeting_Agenda = ? " +
                         "WHERE id = ?"; // Crucial: Update where id matches

            pstmt = conn.prepareStatement(sql);

            // Set the parameters for the prepared statement.
            // The order of setting parameters (1 through 12) matches the '?' placeholders in the SET clause.
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, location);
            pstmt.setString(5, address);
            pstmt.setString(6, meetingType);
            pstmt.setString(7, priority);
            pstmt.setString(8, requiredAttendee);
            pstmt.setString(9, appointmentDate); // JDBC will correctly convert yyyy-MM-dd string to DATE
            pstmt.setString(10, fromTime);       // JDBC will correctly convert HH:mm string to TIME
            pstmt.setString(11, toTime);         // JDBC will correctly convert HH:mm string to TIME
            pstmt.setString(12, meetingAgenda);
            pstmt.setInt(13, id); // Set the ID for the WHERE clause (this is parameter #13)

            // Execute the update query
            int rowsAffected = pstmt.executeUpdate();

            // Check if the update was successful
            if (rowsAffected > 0) {
                // Set a success message in the session and redirect to the view page
                session.setAttribute("message", "Appointment updated successfully!");
                response.sendRedirect("viewAppointments.jsp");
            } else {
                // If no rows were affected, it means the ID wasn't found or no changes were made.
                session.setAttribute("message", "Failed to update appointment. Appointment with ID " + id + " not found or no changes were submitted.");
                // Redirect back to the edit page with the current ID so the user can try again
                response.sendRedirect("editAppointment.jsp?id=" + id);
            }

        } catch (NumberFormatException e) {
            // Handle cases where the ID is not a valid number
            session.setAttribute("message", "Error: Invalid Appointment ID format. Please ensure the ID is a number.");
            response.sendRedirect("viewAppointments.jsp"); // Redirect to a safe page like view
        } catch (SQLException e) {
            // Handle database-specific errors
            session.setAttribute("message", "Database error: " + e.getMessage());
            System.err.println("SQL error updating appointment (ID: " + idStr + "): " + e.getMessage());
            e.printStackTrace();
            // Redirect back to the edit page with the error and the ID to retry
            response.sendRedirect("editAppointment.jsp?id=" + idStr);
        } catch (Exception e) {
            // Catch any other unexpected errors
            session.setAttribute("message", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("editAppointment.jsp?id=" + idStr);
        } finally {
            // Always close JDBC resources in a finally block to prevent leaks
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}