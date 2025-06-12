package web2;

import java.sql.*;
import java.time.LocalDate;

public class AppointmentFetcher {

    public static void main(String[] args) {
        // Change these as per your database setup
    	String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  	    String URL = "jdbc:sqlserver://192.168.1.3:1433;databaseName=AppointmentSchedule;encrypt=true;trustServerCertificate=true";
        String USERNAME = "sa";
        String PASSWORD = "Alp@2023Srv";

        // Replace this with the desired appointment date
        LocalDate appointmentDate = LocalDate.of(2025, 5, 20);

        String query = "SELECT from_time, to_time, name, Meeting_Agenda FROM Schedule_an_Appointment WHERE appointment_date = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setDate(1, Date.valueOf(appointmentDate)); // bind the date parameter

            ResultSet rs = stmt.executeQuery();

            System.out.println("Appointments on " + appointmentDate + ":");
            while (rs.next()) {
                Time fromTime = rs.getTime("from_time");
                Time toTime = rs.getTime("to_time");
                String name = rs.getString("name");
                String agenda = rs.getString("Meeting_Agenda");

                System.out.println("Time: " + fromTime + " - " + toTime);
                System.out.println("Name: " + name);
                System.out.println("Agenda: " + agenda);
                System.out.println("-------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
