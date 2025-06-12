package web2;

import java.sql.*;
import java.util.Random;

public class GenerateAndStoreOtp {

    // Database config
	public static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String URL = "jdbc:sqlserver://192.168.1.3:1433;databaseName=AppointmentSchedule;encrypt=true;trustServerCertificate=true";
	public static final String USERNAME = "sa";
	public static final String PASSWORD = "Alp@2023Srv";

    public static void main(String[] args) {
        int userId = 2;  // Set the UserID for whom you are generating the OTP
        String email = "rautd1066@gmail.com";

        String otp = generateOtp();
        storeOtp(userId, email, otp);
    }
    
    // Generate a 6-digit OTP
    private static String generateOtp() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000); // always 6 digits
        return String.valueOf(otp);
    }

    // Store the OTP in the database
    private static void storeOtp(int userId, String email, String otp) {
        String sql = "INSERT INTO OTP_Records (UserID, Mail, OTP, IsActive) VALUES (?, ?, ?, 1)";

        try {
            // Load the driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, userId);
                ps.setString(2, email);
                ps.setString(3, otp);

                int result = ps.executeUpdate();

                if (result > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int otpId = keys.getInt(1);
                            System.out.println("OTP stored with OTP_ID = " + otpId + ", OTP = " + otp);
                        }
                    }
                } else {
                    System.out.println(" Failed to insert OTP.");
                }

            }

        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database error.");
            e.printStackTrace();
        }
    }
}
