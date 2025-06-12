package com.appointment.schedule;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailServiceOTP {

    /**
     * Sends an email with the generated OTP to the specified recipient.
     *
     * @param recipientEmail The email address of the recipient.
     * @param otp The One-Time Password to be sent.
     * @throws MessagingException If an error occurs during email sending.
     */
    public static void sendOtpEmail(String recipientEmail, String otp) throws MessagingException {
        System.out.println("Attempting to send OTP email to: " + recipientEmail);

        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", DBinfo.EMAIL_HOST);
        properties.put("mail.smtp.port", DBinfo.EMAIL_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS for secure connection
        // For debugging purposes, you can enable logging:
        // properties.put("mail.debug", "true");

        // Get the Session object with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(DBinfo.SENDER_EMAIL, DBinfo.SENDER_PASSWORD);
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(DBinfo.SENDER_EMAIL));

            // Set To: header field
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));

            // Set Subject: header field
            message.setSubject("Your One-Time Password (OTP) for Appointment Schedule");

            // Set the actual message content
            String emailContent = "Dear User,\n\n\n\\n"
                                + "Your One-Time Password (OTP) for your login is: <strong>" + otp + "</strong>\n\n"+"\n\n\n\n"
                                + "\n\n\n\n\nThis OTP is valid for a limited time. Please do not share it with anyone.\n\n\n\n"
                                + "If you did not request this, please ignore this email.\n\n"
                                + "Thanks,\n\n\n\n"
                                + "The Appointment Schedule Team";

            message.setContent(emailContent, "text/html"); // Set content type to HTML for better formatting

            // Send message
            Transport.send(message);
            System.out.println("OTP email sent successfully to " + recipientEmail);

        } catch (MessagingException mex) {
            System.err.println("Error sending OTP email to " + recipientEmail + ": " + mex.getMessage());
            mex.printStackTrace();
            throw mex; // Re-throw the exception to be handled by the calling servlet
        }
    }
}