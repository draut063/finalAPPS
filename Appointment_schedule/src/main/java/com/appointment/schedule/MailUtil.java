package com.appointment.schedule; // Changed package to match DBInfo

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class MailUtil { // Renamed from EmailUtil

    public static boolean sendEmail(String recipientEmail, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", DBinfo.EMAIL_HOST);
        properties.put("mail.smtp.port", DBinfo.EMAIL_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS
        // For debugging, if you want to see detailed mail logs in console:
        // properties.put("mail.debug", "true");
        // properties.put("mail.debug.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(DBinfo.SENDER_EMAIL, DBinfo.SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            // Set sender with a friendly name
            message.setFrom(new InternetAddress(DBinfo.SENDER_EMAIL, DBinfo.SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body, "UTF-8"); // Use UTF-8 for plain text body

            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
            return false;
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error with sender name encoding: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}