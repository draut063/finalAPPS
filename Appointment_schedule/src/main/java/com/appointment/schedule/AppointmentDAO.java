package com.appointment.schedule;

//import com.appointment.schedule.DBInfo; // Import DBInfo
import java.sql.*;
import java.time.LocalDate; // Correct import for LocalDate
import java.time.LocalTime; // Correct import for LocalTime
import java.time.format.DateTimeFormatter; // For formatting date/time in emails

public class AppointmentDAO {

    static {
        try {
            Class.forName(DBinfo.DRIVER); // Use DBInfo for driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load SQL Server JDBC driver.");
        }
    }

    // Utility method to get connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBinfo.URL, DBinfo.USERNAME, DBinfo.PASSWORD);
    }

    public Appointment getAppointmentById(int id) {
        Appointment appointment = null;
        String sql = "SELECT id, name, email, phone, location, address, meeting_type, priority, Required_Attendee, " +
                     "appointment_date, from_time, to_time, Meeting_Agenda, created_at, status " + // Added status here
                     "FROM Schedule_an_Appointment WHERE id = ?";

        try (Connection conn = getConnection(); // Use new getConnection()
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setName(rs.getString("name"));
                appointment.setEmail(rs.getString("email"));
                appointment.setPhone(rs.getString("phone"));
                appointment.setLocation(rs.getString("location"));
                appointment.setAddress(rs.getString("address"));
                appointment.setMeetingType(rs.getString("meeting_type"));
                appointment.setPriority(rs.getString("priority"));
                appointment.setRequiredAttendee(rs.getString("Required_Attendee"));

                Date sqlDate = rs.getDate("appointment_date");
                if (sqlDate != null) {
                    appointment.setAppointmentDate(sqlDate.toLocalDate());
                }

                Time sqlFromTime = rs.getTime("from_time");
                if (sqlFromTime != null) {
                    appointment.setFromTime(sqlFromTime.toLocalTime());
                }

                Time sqlToTime = rs.getTime("to_time");
                if (sqlToTime != null) {
                    appointment.setToTime(sqlToTime.toLocalTime());
                }
                
                appointment.setMeetingAgenda(rs.getString("Meeting_Agenda"));
                
                Timestamp sqlCreatedAt = rs.getTimestamp("created_at");
                if (sqlCreatedAt != null) {
                    appointment.setCreatedAt(sqlCreatedAt.toLocalDateTime());
                }
                // Assuming you'll add a 'status' property to your Appointment model
                // appointment.setStatus(rs.getString("status")); // Add this if you add status to model
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error fetching appointment by ID: " + e.getMessage());
        }
        return appointment;
    }

    /**
     * Updates the status of an appointment.
     * @param appointmentId The ID of the appointment to update.
     * @param status The new status (e.g., "Accepted", "Rejected", "Rescheduled").
     * @return true if update was successful, false otherwise.
     */
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE Schedule_an_Appointment SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error updating appointment status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the date and time of an appointment.
     * @param appointmentId The ID of the appointment to update.
     * @param newDate The new appointment date.
     * @param newFromTime The new 'from' time.
     * @param newToTime The new 'to' time.
     * @return true if update was successful, false otherwise.
     */
    public boolean updateAppointmentDateTime(int appointmentId, LocalDate newDate, LocalTime newFromTime, LocalTime newToTime) {
        String sql = "UPDATE Schedule_an_Appointment SET appointment_date = ?, from_time = ?, to_time = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(newDate));
            pstmt.setTime(2, Time.valueOf(newFromTime));
            pstmt.setTime(3, Time.valueOf(newToTime));
            pstmt.setInt(4, appointmentId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error updating appointment date/time: " + e.getMessage());
            return false;
        }
    }
}