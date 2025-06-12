package com.Emp.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

// Necessary imports for SHA-256 hashing
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// Import your DBConnect class
import com.appointment.schedule.DBConnect;

@WebServlet("/EmployeeRegistrationServlet")
public class EmployeeRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Retrieve form parameters
        String fullName = request.getParameter("fullName");
        String employeeEmail = request.getParameter("employeeEmail");
        String employeeId = request.getParameter("employeeId");
        String newPassword = request.getParameter("newPassword"); // Plain text password
        String confirmPassword = request.getParameter("confirmPassword"); // Get confirm password for server-side check

        // Basic server-side password confirmation check
        if (!newPassword.equals(confirmPassword)) {
            response.sendRedirect("employee-registration.html?error=Passwords do not match.");
            return; // Stop processing if passwords don't match
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 2. Hash the password using SHA-256 (consistent with login servlet and your request)
            String passwordHash = hashPasswordSHA256(newPassword);

            // 3. Get database connection
            connection = DBConnect.getConnection();
            if (connection == null) {
                System.err.println("Failed to get database connection in servlet.");
                throw new SQLException("Failed to get database connection.");
            }

            // 4. Prepare SQL INSERT statement
            // IMPORTANT CHANGE: Using PasswordEmp as per your table schema
            String sql = "INSERT INTO Employee_AppS (EmployeeID, FullName, Email, PasswordEmp, RegistrationDate) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);

            // 5. Set parameters for the prepared statement
            preparedStatement.setString(1, employeeId);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, employeeEmail);
            preparedStatement.setString(4, passwordHash); // Storing the SHA-256 hash here
            preparedStatement.setObject(5, LocalDateTime.now());

            // 6. Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            // 7. Handle success or failure
            if (rowsAffected > 0) {
                System.out.println("Employee " + fullName + " registered successfully.");
                response.sendRedirect("employee-login.html?registrationSuccess=true");
            } else {
                System.out.println("Employee registration failed for " + fullName + ".");
                response.sendRedirect("employee-registration.html?error=Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            System.err.println("SQL error during employee registration: " + e.getMessage());
            e.printStackTrace();
            // Check for specific error codes for duplicate EmployeeID or Email (SQL Server)
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) { // Unique constraint violation
                response.sendRedirect("employee-registration.html?error=Employee ID or Email already exists. Please use a different one.");
            } else {
                response.sendRedirect("employee-registration.html?error=An unexpected database error occurred.");
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing algorithm not found: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("employee-registration.html?error=Internal server error during password processing.");
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("employee-registration.html?error=An unexpected error occurred during registration.");
        } finally {
            // 8. Close resources in a finally block
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
            // Ensure connection is closed even if there are other exceptions
          //  DBConnect.closeConnection(connection); // Re-enabled connection closing
        }
    }

    /**
     * Hashes the plain text password using SHA-256.
     * WARNING: For production, use a stronger, salted, adaptive algorithm like BCrypt or Argon2.
     * SHA-256 is fast and not designed for password hashing, making it vulnerable to brute-force attacks.
     * @param password The plain text password.
     * @return The hashed password as a Base64 encoded string.
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not available.
     */
    private String hashPasswordSHA256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}