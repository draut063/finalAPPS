package com.appointment.schedule; // Changed package to match the updated MailUtil and DBInfo

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date; // For java.sql.Date
import java.sql.Time; // For java.sql.Time
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // For formatting LocalDate/LocalTime in emails

@WebServlet("/a1/mailUpdateAppointment") // Renamed and re-mapped
public class MailUpdateAppointmentServlet extends HttpServlet { // Renamed class
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson(); // Initialize Gson for JSON processing
    private final AppointmentDAO appointmentDAO = new AppointmentDAO(); // Instantiate your DAO

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try (BufferedReader reader = request.getReader()) {
            JsonObject requestBody = gson.fromJson(reader, JsonObject.class);

            int appointmentId = requestBody.get("appointmentId").getAsInt();
            String action = requestBody.get("action").getAsString();

            // Fetch appointment details FIRST to get recipient email and current details
            Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);

            if (appointment == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Appointment not found for ID: " + appointmentId);
                out.print(jsonResponse.toString());
                return;
            }

            String recipientEmail = appointment.getEmail(); // Get email from fetched appointment
            String appointmentName = appointment.getName();
            String meetingAgenda = appointment.getMeetingAgenda();
            LocalDate currentAppointmentDate = appointment.getAppointmentDate();
            LocalTime currentFromTime = appointment.getFromTime();
            LocalTime currentToTime = appointment.getToTime();

            String newStatus = null;
            boolean dbUpdateSuccess = false;

            // --- Database Update Logic ---
            if ("accept".equals(action)) {
                newStatus = "Accepted";
                dbUpdateSuccess = appointmentDAO.updateAppointmentStatus(appointmentId, newStatus);
            } else if ("reject".equals(action)) {
                newStatus = "Rejected";
                dbUpdateSuccess = appointmentDAO.updateAppointmentStatus(appointmentId, newStatus);
            } else if ("change".equals(action)) {
                String newDateStr = requestBody.get("newDate").getAsString();
                String newTimeStr = requestBody.get("newTime").getAsString(); // e.g., "HH:MM - HH:MM"

                // Parse new date and time
                LocalDate newAppointmentDate = LocalDate.parse(newDateStr);
                String[] timeParts = newTimeStr.split("\\s*-\\s*");
                LocalTime newFromTime = LocalTime.parse(timeParts[0]);
                LocalTime newToTime = LocalTime.parse(timeParts[1]);

                newStatus = "Rescheduled";
                dbUpdateSuccess = appointmentDAO.updateAppointmentDateTime(appointmentId, newAppointmentDate, newFromTime, newToTime);
                if (dbUpdateSuccess) {
                    // Also update status to 'Rescheduled'
                    dbUpdateSuccess = appointmentDAO.updateAppointmentStatus(appointmentId, newStatus);
                    // Update the local appointment object for email content
                    currentAppointmentDate = newAppointmentDate;
                    currentFromTime = newFromTime;
                    currentToTime = newToTime;
                }
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid action specified.");
                out.print(jsonResponse.toString());
                return;
            }

            if (dbUpdateSuccess) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("newStatus", newStatus);

                // --- Email Sending Logic ---
                String emailSubject = "";
                String emailBody = "";

                // Use DateTimeFormatter for modern Date/Time API objects
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

                String formattedDate = (currentAppointmentDate != null) ? currentAppointmentDate.format(dateFormatter) : "N/A";
                String formattedFromTime = (currentFromTime != null) ? currentFromTime.format(timeFormatter) : "N/A";
                String formattedToTime = (currentToTime != null) ? currentToTime.format(timeFormatter) : "N/A";
                String timeRange = formattedFromTime + " - " + formattedToTime;

                // Build plain text email body (as per your EmailUtil demo)
                switch (newStatus) {
                    case "Accepted":
                        emailSubject = "Appointment Confirmed: " + (meetingAgenda != null ? meetingAgenda : "Your Meeting");
                        emailBody = String.format(
                            "Dear %s,\n\n" +
                            "We are pleased to inform you that your appointment request has been accepted by the admin.\n\n" +
                            "Appointment Details:\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Agenda: %s\n" +
                            "Reference Number: %d\n\n" +
                            "Thank you, and we look forward to your visit.\n\n" +
                            "Best regards,\n" +
                            "%s",
                            appointmentName, formattedDate, timeRange,
                            (meetingAgenda != null && !meetingAgenda.trim().isEmpty() ? meetingAgenda : "N/A"),
                            appointmentId, DBinfo.SENDER_NAME
                        );
                        break;
                    case "Rejected":
                        emailSubject = "Appointment Rejected: " + (meetingAgenda != null ? meetingAgenda : "Your Meeting");
                        emailBody = String.format(
                            "Dear %s,\n\n" +
                            "We regret to inform you that your appointment (ID: %d) for %s at %s has been rejected.\n\n" +
                            "Meeting Agenda: %s\n\n" +
                            "Please contact us for more details or to schedule a new appointment.\n\n" +
                            "Best regards,\n" +
                            "%s",
                            appointmentName, appointmentId, formattedDate, timeRange,
                            (meetingAgenda != null && !meetingAgenda.trim().isEmpty() ? meetingAgenda : "N/A"),
                            DBinfo.SENDER_NAME
                        );
                        break;
                    case "Rescheduled":
                        emailSubject = "Appointment Rescheduled: " + (meetingAgenda != null ? meetingAgenda : "Your Meeting");
                        emailBody = String.format(
                            "Dear %s,\n\n" +
                            "Your appointment for %s has been RESCHEDULED.\n\n" +
                            "New Details:\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Agenda: %s\n\n" +
                            "Please make a note of the new time and date.\n\n" +
                            "Best regards,\n" +
                            "%s",
                            appointmentName, (meetingAgenda != null ? meetingAgenda : "your meeting"),
                            formattedDate, timeRange,
                            (meetingAgenda != null && !meetingAgenda.trim().isEmpty() ? meetingAgenda : "N/A"),
                            DBinfo.SENDER_NAME
                        );
                        break;
                }

                if (recipientEmail != null && !recipientEmail.isEmpty()) {
                    boolean emailSent = MailUtil.sendEmail(recipientEmail, emailSubject, emailBody);
                    if (emailSent) {
                        System.out.println("Email notification sent for action: " + action + " to " + recipientEmail);
                    } else {
                        System.err.println("Failed to send email notification for action: " + action + " to " + recipientEmail);
                    }
                } else {
                    System.err.println("Recipient email not found or empty for appointment ID: " + appointmentId);
                }

            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update appointment status/details in DB.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        } finally {
            out.print(jsonResponse.toString());
            out.flush();
            out.close();
        }
    }
}