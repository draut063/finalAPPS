package com.appointment.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// You'll need a JSON library, e.g., org.json.
// Add dependency: org.json:json:20231013 (or latest) to your pom.xml or build.gradle
import org.json.JSONObject;
import org.json.JSONException;

@WebServlet("/a1/updateAppointment")
public class StatusUpdateAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        
        String action = "";
        int appointmentId = 0;
        String newDateStr = null;
        String newFromTimeStr = null;
        String newToTimeStr = null;
        String updatedStatusMessage = ""; // To send back the status text e.g. "Accepted"

        try {
            JSONObject jsonRequest = new JSONObject(requestBody);
            action = jsonRequest.getString("action");
            appointmentId = jsonRequest.getInt("appointmentId");

            if ("change".equals(action)) {
                newDateStr = jsonRequest.getString("newDate");
                String newTimeRangeStr = jsonRequest.getString("newTime"); // Expects "HH:mm - HH:mm"
                
                String[] timeParts = newTimeRangeStr.split("\\s*-\\s*"); // Split by hyphen with optional spaces
                if (timeParts.length == 2 && 
                    timeParts[0].matches("\\d{2}:\\d{2}") && 
                    timeParts[1].matches("\\d{2}:\\d{2}")) {
                    newFromTimeStr = timeParts[0];
                    newToTimeStr = timeParts[1];
                } else {
                    throw new JSONException("Invalid time format for 'change' action. Expected 'HH:mm - HH:mm'.");
                }
            }
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid JSON request data: " + escapeJson(e.getMessage()) + "\"}");
            return;
        }

        String sql = "";
        boolean success = false;

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Start transaction

            if ("accept".equals(action)) {
                // Can accept if Pending or Rescheduled
                sql = "UPDATE Schedule_an_Appointment SET status = 'Accepted' WHERE id = ? AND (status = 'Pending' OR status = 'Rescheduled')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, appointmentId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        success = true;
                        updatedStatusMessage = "Accepted";
                    } else {
                         // Could be that the appointment was not in 'Pending' or 'Rescheduled' state, or ID not found
                        updatedStatusMessage = "No action taken (already accepted or not found).";
                    }
                }
            } else if ("reject".equals(action)) {
                 // Can reject if Pending, Accepted or Rescheduled
                sql = "UPDATE Schedule_an_Appointment SET status = 'Rejected' WHERE id = ? AND (status = 'Pending' OR status = 'Accepted' OR status = 'Rescheduled')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, appointmentId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        success = true;
                        updatedStatusMessage = "Rejected";
                    } else {
                        updatedStatusMessage = "No action taken (already rejected/cancelled or not found).";
                    }
                }
            } else if ("change".equals(action)) {
                sql = "UPDATE Schedule_an_Appointment SET appointment_date = ?, from_time = ?, to_time = ?, status = 'Rescheduled' WHERE id = ? AND (status = 'Pending' OR status = 'Accepted' OR status = 'Rescheduled')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(newDateStr);
                    stmt.setDate(1, new java.sql.Date(parsedDate.getTime()));

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    
                    java.util.Date parsedFromTimeUtil = timeFormat.parse(newFromTimeStr);
                    stmt.setTime(2, new java.sql.Time(parsedFromTimeUtil.getTime()));

                    java.util.Date parsedToTimeUtil = timeFormat.parse(newToTimeStr);
                    stmt.setTime(3, new java.sql.Time(parsedToTimeUtil.getTime()));
                    
                    stmt.setInt(4, appointmentId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        success = true;
                        updatedStatusMessage = "Rescheduled";
                    } else {
                         updatedStatusMessage = "No action taken (appointment not eligible for change or not found).";
                    }
                } catch (ParseException e) {
                    conn.rollback(); // Rollback on parsing error during change
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"message\": \"Error parsing date or time: " + escapeJson(e.getMessage()) + "\"}");
                    return;
                }
            } else {
                conn.rollback(); // Rollback for invalid action
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid action specified.\"}");
                return;
            }

            if (success) {
                conn.commit();
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"newStatus\": \"" + escapeJson(updatedStatusMessage) + "\", \"appointmentId\": " + appointmentId + "}");
            } else {
                conn.rollback(); // Rollback if no rows affected but no specific error
                response.setContentType("application/json");
                // SC_OK might be better if the operation was valid but simply didn't change anything
                // (e.g. trying to accept an already accepted item and DB query handles that condition)
                // For simplicity, if success flag is not true, treat as some form of non-success for client.
                response.getWriter().write("{\"success\": false, \"message\": \"Appointment update failed or no changes made. " + escapeJson(updatedStatusMessage) + "\"}");
            }

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Database error: " + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) { // Catch any other unexpected errors
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"An unexpected error occurred: " + escapeJson(e.getMessage()) + "\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        // Basic JSON escaping
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}