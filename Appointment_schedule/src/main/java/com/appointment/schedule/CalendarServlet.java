package com.appointment.schedule;

import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/a1/appointments")
public class CalendarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dateStr = request.getParameter("date");
        StringBuilder json = new StringBuilder();
        json.append("[");

        // It's good practice to fetch appointments regardless of status for a director's view,
        // and let the frontend logic (JS) decide which actions are permissible based on status.
        // SUGGESTION: Consider ordering by time to ensure consistent display.
        String sql = "SELECT id, from_time, to_time, name, Meeting_Agenda, status FROM Schedule_an_Appointment WHERE appointment_date = ? ORDER BY from_time";


        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, dateStr);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }
                    first = false;

                    String fromTimeStr = rs.getString("from_time");
                    String toTimeStr = rs.getString("to_time");
                    
                    String timeRange = "";
                    if (fromTimeStr != null) {
                        // Ensure it's in HH:MM format (substring(0, 5) handles TIME types)
                        timeRange += fromTimeStr.substring(0, 5); 
                    }
                    if (toTimeStr != null) {
                        if (!timeRange.isEmpty()) {
                             timeRange += " - ";
                        }
                        // Ensure it's in HH:MM format
                        timeRange += toTimeStr.substring(0, 5); 
                    }


                    json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"time\":\"").append(escapeJson(timeRange)).append("\",")
                        .append("\"name\":\"").append(escapeJson(rs.getString("name"))).append("\",")
                        .append("\"agenda\":\"").append(escapeJson(rs.getString("Meeting_Agenda"))).append("\",")
                        .append("\"status\":\"").append(escapeJson(rs.getString("status"))).append("\"")
                        .append("}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log this error properly in a real application
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Database error fetching appointments.\"}");
            return; // Important to return after sending error response
        }

        json.append("]");     

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}