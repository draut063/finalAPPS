<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.appointment.schedule.DBConnect" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.net.URLEncoder" %>

<%
    // CRITICAL: Retrieve employee details from the SESSION, not form parameters!
    HttpSession currentSession = request.getSession(false);

    // If session is null or required attributes are missing, redirect to login
    if (currentSession == null || currentSession.getAttribute("loggedInEmployeeName") == null ||
        currentSession.getAttribute("loggedInEmployeeEmail") == null ||
        currentSession.getAttribute("loggedInEmployeeId") == null) {
        response.sendRedirect("employee-login.html");
        return;
    }

    String name = (String) currentSession.getAttribute("loggedInEmployeeName");
    String email = (String) currentSession.getAttribute("loggedInEmployeeEmail");
    String empId = (String) currentSession.getAttribute("loggedInEmployeeId");

    // Retrieve other form parameters
    String location = request.getParameter("location");
    String personToMeet = request.getParameter("personToMeet");
    String requiredAttendeeName = request.getParameter("requiredAttendeeName");
    String appointmentDate = request.getParameter("appointmentDate");
    String fromTime = request.getParameter("fromTime"); // This is the new appointment's start time
    String toTime = request.getParameter("toTime");     // This is the new appointment's end time
    String meetingAgenda = request.getParameter("reason");

    String phone = "";
    String address = "";
    String meetingType = "Offline";
    String priority = "Medium";

    String finalRequiredAttendee;
    if (requiredAttendeeName != null && !requiredAttendeeName.trim().isEmpty()) {
        finalRequiredAttendee = requiredAttendeeName.trim();
    } else if (personToMeet != null && !personToMeet.trim().isEmpty()) {
        finalRequiredAttendee = personToMeet.trim();
    } else {
        finalRequiredAttendee = "N/A";
    }

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean success = false;
    String message = "";

    try {
        con = DBConnect.getConnection();
        if (con == null) {
            throw new SQLException("Database connection is null.");
        }

        // --- Start of Robust Time Slot Validation ---
        // This query checks if *any* existing appointment on the same date
        // overlaps with the new requested time slot (fromTime, toTime).
        // The condition NOT (existing_to_time <= new_from_time OR existing_from_time >= new_to_time)
        // correctly identifies all overlaps, including partial overlaps and containment.
        String checkOverlapSql = "SELECT COUNT(*) FROM Schedule_an_Appointment "
                               + "WHERE appointment_date = ? "
                               + "AND NOT (to_time <= ? OR from_time >= ?)";

        ps = con.prepareStatement(checkOverlapSql);
        ps.setString(1, appointmentDate); // Parameter 1: new appointment_date
        ps.setString(2, fromTime);        // Parameter 2: new from_time (B_start)
        ps.setString(3, toTime);          // Parameter 3: new to_time (B_end)

        rs = ps.executeQuery();

        if (rs.next() && rs.getInt(1) > 0) {
            // Overlap detected for the selected date and time slot
            message = "An appointment already exists for " + appointmentDate + " within the " + fromTime + " to " + toTime + " time slot. Please choose a different time.";
            success = false;
            // Close resources used for the check query before redirection
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            response.sendRedirect("Emp_Schedule_an_Appointment.jsp?status=error&message=" + URLEncoder.encode(message, "UTF-8"));
            return; // Stop further processing
        }
        // --- End of Robust Time Slot Validation ---


        // If no overlap was found, proceed with the insertion.
        String insertSql = "INSERT INTO Schedule_an_Appointment " +
                           "(name, email, phone, EMP_ID, location, address, meeting_type, priority, Required_Attendee, appointment_date, from_time, to_time, Meeting_Agenda) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ps = con.prepareStatement(insertSql); // Re-prepare for insertion

        // Set parameters for the PreparedStatement for insertion
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone.isEmpty() ? null : phone);
        ps.setString(4, empId);
        ps.setString(5, location);
        ps.setString(6, address.isEmpty() ? null : address);
        ps.setString(7, meetingType);
        ps.setString(8, priority);
        ps.setString(9, finalRequiredAttendee);
        ps.setString(10, appointmentDate);
        ps.setString(11, fromTime);
        ps.setString(12, toTime);
        ps.setString(13, meetingAgenda);

        int rowsAffected = ps.executeUpdate();

        if (rowsAffected > 0) {
            success = true;
            message = "Appointment scheduled successfully!";
        } else {
            message = "Failed to schedule appointment. No rows affected.";
        }

    } catch (SQLException e) {
        message = "A database error occurred: " + e.getMessage();
        System.err.println("SQL Error during appointment submission: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        message = "An unexpected error occurred: " + e.getMessage();
        System.err.println("An unexpected error occurred during appointment submission: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Close resources
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.err.println("Error closing JDBC resources: " + e.getMessage());
        }
    }

    // Redirect back to the form with a status parameter and the message
    if (success) {
        response.sendRedirect("Emp_Schedule_an_Appointment.jsp?status=success&message=" + URLEncoder.encode(message, "UTF-8"));
    } else {
        response.sendRedirect("Emp_Schedule_an_Appointment.jsp?status=error&message=" + URLEncoder.encode(message, "UTF-8"));
    }
%>