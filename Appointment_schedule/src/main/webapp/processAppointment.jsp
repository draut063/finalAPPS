<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.appointment.schedule.DBConnect" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    // Initialize variables
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String location = request.getParameter("location");
    String address = request.getParameter("address");
    String meetingType = request.getParameter("meetingType");
    String priority = request.getParameter("priority");
    String personToMeet = request.getParameter("personToMeet");
    String appointmentDate = request.getParameter("appointmentDate");
    String fromTime = request.getParameter("fromTime");
    String toTime = request.getParameter("toTime");
    String reason = request.getParameter("reason");

    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean success = false;
    String message = ""; // To store success or error messages

    try {
        conn = DBConnect.getConnection();

        // --- Start of Time Slot Validation ---
        String checkOverlapSql = "SELECT COUNT(*) FROM Schedule_an_Appointment "
                               + "WHERE appointment_date = ? "
                               + "AND ( "
                               + "    (? < to_time AND ? > from_time) " // New appointment starts before existing ends AND new appointment ends after existing starts
                               + "    OR (? = from_time OR ? = to_time) " // New appointment exact start or end time matches existing
                               + "    OR (? > from_time AND ? < to_time) " // New appointment is fully contained within an existing one
                               + ")";

        pstmt = conn.prepareStatement(checkOverlapSql);
        pstmt.setString(1, appointmentDate);
        pstmt.setString(2, fromTime);
        pstmt.setString(3, toTime);
        pstmt.setString(4, fromTime);
        pstmt.setString(5, toTime);
        pstmt.setString(6, fromTime);
        pstmt.setString(7, toTime);

        rs = pstmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            // Overlap detected
            message = "An appointment already exists for the selected date and time slot. Please choose a different time.";
            success = false;
        } else {
            // No overlap, proceed to insert the new appointment
            String insertSql = "INSERT INTO Schedule_an_Appointment ("
                             + "name, email, phone, location, address, meeting_type, priority, Required_Attendee, "
                             + "appointment_date, from_time, to_time, Meeting_Agenda) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);

            // Set parameters for insertion
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, location);
            pstmt.setString(5, address);
            pstmt.setString(6, meetingType);
            pstmt.setString(7, priority);
            pstmt.setString(8, personToMeet);
            pstmt.setString(9, appointmentDate);
            pstmt.setString(10, fromTime);
            pstmt.setString(11, toTime);
            pstmt.setString(12, reason);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                success = true;
                message = "Appointment scheduled successfully!";
                System.out.println("Appointment scheduled successfully!");
            } else {
                message = "Failed to schedule appointment. Please try again.";
                System.out.println("Failed to schedule appointment.");
            }
        }
        // --- End of Time Slot Validation ---

    } catch (SQLException e) {
        message = "A database error occurred: " + e.getMessage();
        System.err.println("Database error: " + e.getMessage());
        e.printStackTrace();
    } finally {
        if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
        if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
        if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }

    // Redirect back to the form page with status and message
    if (success) {
        response.sendRedirect("Schedule_an_appointment.jsp?status=success&message=" + java.net.URLEncoder.encode(message, "UTF-8"));
    } else {
        response.sendRedirect("Schedule_an_appointment.jsp?status=error&message=" + java.net.URLEncoder.encode(message, "UTF-8"));
    }
%>