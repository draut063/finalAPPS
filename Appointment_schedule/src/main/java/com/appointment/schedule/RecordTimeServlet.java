package com.appointment.schedule;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; // Import for java.sql.Timestamp
import java.time.LocalDateTime; // Import for java.time.LocalDateTime

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/T1/RecordTimeServlet")
public class RecordTimeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String actionType = request.getParameter("actionType");
        String appointmentIdStr = request.getParameter("appointmentId");
        int appointmentId; // No default needed, will be assigned after parse or throw error

        // Basic validation for appointmentId
        if (appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            sendErrorRedirect(request, response, "Appointment ID is missing.");
            return;
        }
        try {
            appointmentId = Integer.parseInt(appointmentIdStr);
        } catch (NumberFormatException e) {
            sendErrorRedirect(request, response, "Invalid Appointment ID format.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection(); // Get database connection
            conn.setAutoCommit(false); // Start transaction for atomicity

            if ("In-Time".equals(actionType)) {
                String getPassNo = request.getParameter("getPassNoForInTime"); // Retrieve Get Pass No.

                if (getPassNo == null || getPassNo.trim().isEmpty()) {
                    conn.rollback(); // Rollback transaction on error
                    sendErrorRedirect(request, response, "Get Pass No. is required for In-Time.");
                    return;
                }

                // Check if an entry already exists and if in_time is already recorded
                String checkSql = "SELECT id, in_time, out_time FROM In_Out_Time WHERE appointment_id = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setInt(1, appointmentId);
                rs = pstmt.executeQuery();

                boolean entryExists = rs.next();
                Timestamp existingInTime = null;
                if (entryExists) {
                    existingInTime = rs.getTimestamp("in_time");
                }
                // Close ResultSet and PreparedStatement immediately after use to free resources
                rs.close();
                pstmt.close();

                if (entryExists && existingInTime != null) {
                    // In-Time already recorded for this appointment
                    conn.rollback();
                    sendErrorRedirect(request, response, "In-Time already recorded for this appointment.");
                    return;
                } else if (entryExists) {
                    // Entry exists but in_time is NULL, update it (and get_pass_no)
                    String updateSql = "UPDATE In_Out_Time SET get_pass_no = ?, in_time = ? WHERE appointment_id = ?";
                    pstmt = conn.prepareStatement(updateSql);
                    pstmt.setString(1, getPassNo.trim());
                    pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
                    pstmt.setInt(3, appointmentId);
                    pstmt.executeUpdate();
                } else {
                    // No entry exists, insert a new record
                    String insertSql = "INSERT INTO In_Out_Time (appointment_id, get_pass_no, in_time) VALUES (?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSql);
                    pstmt.setInt(1, appointmentId);
                    pstmt.setString(2, getPassNo.trim());
                    pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
                    pstmt.executeUpdate();
                }

                conn.commit(); // Commit transaction on success
                sendSuccessRedirect(request, response, "In-Time and Get Pass No. recorded successfully!");

            } else if ("Out-Time".equals(actionType)) {
                // Ensure In-Time has been recorded and Out-Time has not
                String checkInOutTimeSql = "SELECT in_time, out_time FROM In_Out_Time WHERE appointment_id = ?";
                pstmt = conn.prepareStatement(checkInOutTimeSql);
                pstmt.setInt(1, appointmentId);
                rs = pstmt.executeQuery();

                boolean entryFound = rs.next();
                Timestamp recordedInTime = null;
                Timestamp recordedOutTime = null;
                if (entryFound) {
                    recordedInTime = rs.getTimestamp("in_time");
                    recordedOutTime = rs.getTimestamp("out_time");
                }
                rs.close();
                pstmt.close();

                if (!entryFound || recordedInTime == null) {
                    conn.rollback();
                    sendErrorRedirect(request, response, "Cannot record Out-Time; In-Time not recorded or entry missing.");
                    return;
                }
                if (recordedOutTime != null) {
                     conn.rollback();
                     sendErrorRedirect(request, response, "Out-Time already recorded for this appointment.");
                     return;
                }

                // Update out_time for the existing record
                String updateSql = "UPDATE In_Out_Time SET out_time = ? WHERE appointment_id = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
                pstmt.setInt(2, appointmentId);
                pstmt.executeUpdate();

                conn.commit(); // Commit transaction
                sendSuccessRedirect(request, response, "Out-Time recorded successfully!");

            } else {
                conn.rollback();
                sendErrorRedirect(request, response, "Invalid action.");
            }

        } catch (SQLException e) {
            // Rollback on any SQL exception
            try { if (conn != null) conn.rollback(); } catch (SQLException rbk) { /* Log rollback error if needed */ }
            System.err.println("Database error in RecordTimeServlet: " + e.getMessage());
            e.printStackTrace();
            sendErrorRedirect(request, response, "Database error: " + e.getMessage());
        } catch (Exception e) {
            // Rollback on any other exception
            try { if (conn != null) conn.rollback(); } catch (SQLException rbk) { /* Log rollback error if needed */ }
            System.err.println("An unexpected error occurred in RecordTimeServlet: " + e.getMessage());
            e.printStackTrace();
            sendErrorRedirect(request, response, "An unexpected error occurred: " + e.getMessage());
        } finally {
            // Close resources in finally block to ensure they are closed
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Helper method for sending success redirects
    private void sendSuccessRedirect(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        response.sendRedirect(request.getContextPath() + "/SecurityPage.jsp?success=" + URLEncoder.encode(message, "UTF-8"));
    }

    // Helper method for sending error redirects
    private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        response.sendRedirect(request.getContextPath() + "/SecurityPage.jsp?error=" + URLEncoder.encode(message, "UTF-8"));
    }
}