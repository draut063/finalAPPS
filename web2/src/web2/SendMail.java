//package web2;
//
//import javax.mail.*;
//import javax.mail.internet.*;
//import java.util.Properties;
//
//public class SendMail {
//
//    public static void main(String[] args) {
//
//        // Sender's email ID and password
//        final String username = "your_email@example.com"; // Your email address
//        final String password = "your_email_password"; // Your email password or App Password
//
//        // Recipient's email ID
//        String recipientEmail = "recipient_email@example.com"; // Recipient's email address
//
//        // Email properties for SMTP
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true"); // Enable authentication
//        props.put("mail.smtp.starttls.enable", "true"); // Enable TLS
//        props.put("mail.smtp.host", "smtp.example.com"); // SMTP server host (e.g., smtp.gmail.com for Gmail)
//        props.put("mail.smtp.port", "587"); // SMTP port (e.g., 587 for TLS, 465 for SSL)
//
//        // For Gmail, use:
//        // props.put("mail.smtp.host", "smtp.gmail.com");
//        // props.put("mail.smtp.port", "587");
//
//        // Get the Session object and pass username and password
//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//            // Create a default MimeMessage object
//            Message message = new MimeMessage(session);
//
//            // Set From: header field
//            message.setFrom(new InternetAddress(username));
//
//            // Set To: header field
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
//
//            // Set Subject: header field
//            message.setSubject("Test Email from Java");
//
//            // Set the actual message
//            message.setText("This is a test email sent from a Java application using SMTP!");
//
//            // Send message
//            Transport.send(message);
//
//            System.out.println("Email sent successfully!");
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.err.println("Error sending email: " + e.getMessage());
//        }
//    }
//}