package web2;

import java.sql.Connection;
import java.sql.DriverManager;


public class JDBC {
	
     static  String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
     static  String URL = "jdbc:sqlserver://192.168.1.3:1433;databaseName=AppointmentSchedule;encrypt=true;trustServerCertificate=true";
     static  String USERNAME = "sa";
     static  String PASSWORD = "Alp@2023Srv";

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName(DRIVER); // Load the JDBC driver
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println(" Connected to SQL Server database!");
            connection.close();
        }  catch (Exception e) {
            System.out.println(" Connection failed.");
            e.printStackTrace();
        } 
    }
}
