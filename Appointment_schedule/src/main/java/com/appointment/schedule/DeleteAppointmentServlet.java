package com.appointment.schedule; // Or your actual package name

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// If you are on Tomcat 9 or older (javax.servlet.*) use these imports:
// import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;


@WebServlet("/DeleteAppointmentServlet")
public class DeleteAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int appointmentId = 0;

        if (idStr != null && !idStr.isEmpty()) {
            try {
                appointmentId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid appointment ID format: " + idStr);
                // Optionally, set an error message and redirect to an error page or back with a message
                response.sendRedirect("viewAppointments.jsp?error=Invalid ID"); // Assuming viewAppointments.jsp is your listing page
                return;
            }
        } else {
            System.err.println("Appointment ID not provided for deletion.");
            response.sendRedirect("viewAppointments.jsp?error=ID missing");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnect.getConnection();
            String sql = "DELETE FROM Schedule_an_Appointment WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, appointmentId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Appointment with ID " + appointmentId + " deleted successfully.");
                // Optionally, set a success message for the user
                response.sendRedirect("viewAppointments.jsp?success=Appointment deleted");
            } else {
                System.out.println("No appointment found with ID " + appointmentId + " to delete or deletion failed.");
                response.sendRedirect("viewAppointments.jsp?error=Deletion failed or appointment not found");
            }

        } catch (SQLException e) {
            System.err.println("SQL Error during appointment deletion: " + e.getMessage());
            e.printStackTrace();
            // In a real app, you might want to redirect to an error page with more details
            // or set a request attribute with the error message and forward.
            response.sendRedirect("viewAppointments.jsp?error=Database error during deletion");
        } catch (Exception e) { // Catch other potential errors e.g. from DBConnect
            System.err.println("General Error during appointment deletion: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("viewAppointments.jsp?error=Unexpected error during deletion");
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // It's good practice to also implement doPost and have it call doGet if
    // you might submit to this servlet via a POST method in the future.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}