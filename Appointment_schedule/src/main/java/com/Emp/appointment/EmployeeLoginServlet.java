package com.Emp.appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // For managing user sessions
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// Import DBConnect from its package
import com.appointment.schedule.DBConnect;

@WebServlet("/EmployeeLoginServlet") // Maps this servlet to the URL
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Retrieve form parameters
        String employeeId = request.getParameter("employeeId");
        String password = request.getParameter("password"); // Plain text password entered by user

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 2. Hash the entered password using SHA-256 for comparison
            String hashedEnteredPassword = hashPasswordSHA256(password);

            // 3. Get database connection
            connection = DBConnect.getConnection();
            if (connection == null) {
                System.err.println("Failed to get database connection in login servlet.");
                throw new SQLException("Failed to get database connection.");
            }

            // 4. Prepare SQL SELECT statement to retrieve employee by ID and password hash
            // Changed PasswordHash to PasswordEmp as per your new table schema
            // Now also selecting 'Email' to store in session and log
            String sql = "SELECT EmployeeID, FullName, Email, PasswordEmp FROM Employee_AppS WHERE EmployeeID = ? AND PasswordEmp = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, employeeId);
            preparedStatement.setString(2, hashedEnteredPassword); // Compare with the hashed entered password

            // 5. Execute the query
            resultSet = preparedStatement.executeQuery();

            // 6. Check if employee exists (meaning EmployeeID and hashed password matched)
            if (resultSet.next()) {
                String fullName = resultSet.getString("FullName");
                String email = resultSet.getString("Email"); 

                // Login successful!
                System.out.println("Employee " + fullName + " (ID: " + employeeId + ", Email: " + email + ") logged in successfully.");

                // 7. Create or get existing session
                HttpSession session = request.getSession();
                session.setAttribute("loggedInEmployeeId", employeeId);
                session.setAttribute("loggedInEmployeeName", fullName);
                session.setAttribute("loggedInEmployeeEmail", email); // Store the email in session
                session.setMaxInactiveInterval(30 * 60); // Session timeout in seconds (e.g., 30 minutes)

                // 8. Redirect to a protected dashboard or home page
                response.sendRedirect("Emp_Schedule_an_Appointment.jsp");
            } else {
                // Employee ID not found OR password did not match the stored hash
                System.out.println("Login failed for Employee ID " + employeeId + ": Invalid credentials.");
                response.sendRedirect("employee-login.html?error=Invalid Employee ID or password.");
            }

        } catch (SQLException e) {
            System.err.println("SQL error during employee login: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("employee-login.html?error=An unexpected database error occurred.");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing algorithm not found: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("employee-login.html?error=Internal server error during password processing.");
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.err.println("An unexpected error occurred during login: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("employee-login.html?error=An unexpected error occurred during login.");
        } finally {
            // 9. Close resources in a finally block
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                // Close connection only if DBConnect doesn't manage pooling (e.g., if it provides a fresh connection)
                // If DBConnect handles connection pooling, avoid closing it here.
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing JDBC resources: " + e.getMessage());
            }
        }
    }

    /**
     * Hashes a password using SHA-256 algorithm.
     * This method should be consistent with the one used in EmployeeRegistrationServlet.
     * @param password The plain-text password to hash.
     * @return The SHA-256 hashed password encoded in Base64.
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not available.
     */
    private String hashPasswordSHA256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}