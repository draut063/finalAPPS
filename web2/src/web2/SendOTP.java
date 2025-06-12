//package web2;
//
//import java.sql.*;
//import java.util.*;
//
//public class SendOTP {
//
//    // Database config
//	 static String URL = "jdbc:sqlserver://192.168.1.3:1433;databaseName=AppointmentSchedule;encrypt=true;trustServerCertificate=true";
//     static String USERNAME = "sa";
//     static String PASSWORD = "Alp@2023Srv";
//
//
//    // Email config
//    private static final String FROM_EMAIL = "rautd1066@gmail.com";
//    private static final String FROM_PASSWORD = "Raut@1066"; // App password if using Gmail
//
//    public static void main(String[] args) {
//        int userId = 2; // Replace with actual UserID from AdminUsers
//        String recipientEmail = "shivanidsharme99@gmail.com"; // User's email
//
//        String otp = generateOtp();
//
//        if (storeOtpInDatabase(userId, recipientEmail, otp)) {
//            sendEmail(recipientEmail, otp);
//        }
//    }
//
//    // Generate a 6-digit OTP
//    private static String generateOtp() {
//        Random rand = new Random();
//        return String.valueOf(100000 + rand.nextInt(900000));
//    }
//
//    // Store OTP in the database
//    private static boolean storeOtpInDatabase(int userId, String email, String otp) {
//        String sql = "INSERT INTO OTP_Records (UserID, Mail, OTP, IsActive) VALUES (?, ?, ?, 1)";
//
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
//            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//                 PreparedStatement ps = conn.prepareStatement(sql)) {
//
//                ps.setInt(1, userId);
//                ps.setString(2, email);
//                ps.setString(3, otp);
//
//                int inserted = ps.executeUpdate();
//
//                if (inserted > 0) {
//                    System.out.println("OTP stored successfully for user.");
//                    return true;
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    // Send OTP via email
//    private static void sendEmail(String toEmail, String otp) {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(FROM_EMAIL));
//            message.setRecipients(
//                    Message.RecipientType.TO, InternetAddress.parse(toEmail));
//            message.setSubject("Your OTP Code");
//            message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
//
//            Transport.send(message);
//
//            System.out.println("✅ OTP sent to " + toEmail);
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.out.println("❌ Failed to send OTP email.");
//        }
//    }
//}
