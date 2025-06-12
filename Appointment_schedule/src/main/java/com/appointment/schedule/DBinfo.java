package com.appointment.schedule;

public interface DBinfo {
	
	//DB Connectivity Configuration
	public static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String URL = "jdbc:sqlserver://192.168.1.3:1433;databaseName=AppointmentSchedule;encrypt=true;trustServerCertificate=true";
	public static final String USERNAME = "sa";
	public static final String PASSWORD = "Alp@2023Srv";
	

	    // Email Configuration (from your EmailUtil.java)
	    public static final String EMAIL_HOST = "outlook.office365.com"; 
	    public static final String EMAIL_PORT = "25"; 
	    public static final String SENDER_EMAIL = "postmyquery@scdl.net"; 
	    public static final String SENDER_PASSWORD = "P0$t#4#6My#q5$4Ps6";
	    public static final String SENDER_NAME = "SCDL";
	    
	}