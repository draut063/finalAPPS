<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="com.appointment.schedule.DBConnect" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Security - View Appointments</title>
    <style>
        /* General Body Styles */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            color: #333;
        }

        /* Navbar Styling */
        .navbar {
            width: 100vw;
            background-color: #f9e9de;
            color: black;
            padding: 15px 20px;
            font-size: 22px;
            font-weight: bold;
            position: fixed;
            top: 0;
            left: 0;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            z-index: 1000;
        }

        .logo-container {
            margin-left: 15px;
            display: flex;
            align-items: center;
        }

        .logo-container img {
            height: 50px;
            margin-right: 10px;
        }

        .navbar-actions {
            display: flex;
            align-items: center;
        }

        .navbar-actions .action-link a {
            color: #007BFF;
            text-decoration: none;
            font-size: 16px;
            padding: 5px 10px;
            border: 1px solid #007BFF;
            border-radius: 4px;
            transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
            white-space: nowrap;
        }

        .navbar-actions .action-link a:hover {
            background-color: #007BFF;
            color: white;
            border-color: #0056b3;
        }

        /* Specific styling for the 'Logout' link */
        .action-link.logout-link {
             margin-right: 45px;
        }

        /* Main Content Area */
        .container {
            margin-top: 90px;
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 25px;
            font-size: 28px;
        }

        /* Table Styling */
        .appointments-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.05);
        }

        .appointments-table th, .appointments-table td {
            border: 1px solid #ddd;
            padding: 12px 15px;
            text-align: left;
        }

        .appointments-table th {
            background-color: #f2f2f2;
            color: #555;
            font-weight: bold;
            text-transform: uppercase;
            font-size: 14px;
        }

        .appointments-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .appointments-table tr:hover {
            background-color: #e9e9e9;
        }

        /* --- NEW/UPDATED STYLE FOR SR. NO. COLUMN --- */
        .appointments-table th.sr-no-col,
        .appointments-table td.sr-no-col {
            width: 5%; /* Adjust width as needed, e.g., 5%, 50px */
            text-align: center; /* Center the number */
        }
        /* --- END OF NEW/UPDATED STYLE --- */

        /* No Appointments Message */
        .no-appointments {
            text-align: center;
            padding: 30px;
            font-size: 1.1em;
            color: #777;
        }
        .error-message {
            color: red;
            text-align: center;
            padding: 10px;
            border: 1px solid red;
            background-color: #ffebeb;
            margin-bottom: 20px;
        }

        /* Responsive adjustments */
        @media (max-width: 768px) {
            body {
                padding: 10px;
            }
            .navbar {
                flex-direction: column;
                align-items: flex-start;
                padding: 10px;
            }
            .logo-container {
                margin-bottom: 10px;
            }
            .navbar-actions {
                flex-direction: column;
                width: 100%;
            }
            .action-link {
                width: 100%;
                text-align: center;
                margin-bottom: 10px;
            }
            .action-link:last-child {
                margin-bottom: 0;
            }
            .action-link a {
                display: block;
                border: 1px solid #007BFF;
                padding: 10px 0;
            }
            .container {
                margin-top: 150px;
                padding: 10px;
            }
            .appointments-table, .appointments-table tbody, .appointments-table tr, .appointments-table td, .appointments-table th {
                display: block;
            }
            .appointments-table thead {
                display: none;
            }
            .appointments-table tr {
                margin-bottom: 15px;
                border: 1px solid #ddd;
                border-radius: 5px;
                overflow: hidden;
            }
            .appointments-table td {
                text-align: right;
                padding-left: 50%;
                position: relative;
                border-bottom: 1px solid #eee;
            }
            .appointments-table td:last-child {
                border-bottom: none;
            }
            .appointments-table td::before {
                content: attr(data-label);
                position: absolute;
                left: 15px;
                width: calc(50% - 30px);
                text-align: left;
                font-weight: bold;
                color: #555;
            }
        }

          .appointments-table td form {
            display: inline-block;
            margin: 2px;
            vertical-align: top; /* Align forms at the top */
        }

        .appointments-table input[type="submit"] {
            padding: 5px 10px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }

        /* Specific style for enabled In-Time button hover */
        .appointments-table input[type="submit"]:not(.out-time-btn):hover:enabled {
            background-color: #218838;
        }

        /* Specific style for disabled In-Time button */
        .appointments-table input[type="submit"]:not(.out-time-btn):disabled {
            background-color: #d4edda; /* Faded green */
            color: #6c757d; /* Darker grey text */
            cursor: not-allowed;
            border: 1px solid #c3e6cb; /* Lighter border */
        }

        .appointments-table input[type="submit"].out-time-btn { /* Specific class for out-time button */
            background-color: #dc3545;
        }

        .appointments-table input[type="submit"].out-time-btn:hover:enabled { /* Only hover if not disabled */
            background-color: #c82333;
        }
        
        /* Specific style for disabled out-time button */
        .appointments-table input[type="submit"].out-time-btn:disabled {
            background-color: #f0f0f0; /* Lighter background for disabled out-time */
            color: #a0a0a0; /* Grey text for disabled out-time */
            border: 1px solid #dcdcdc; /* Lighter border */
            cursor: not-allowed;
        }

        /* Styles for the "Get Pass No." input field */
        .get-pass-input {
            width: 90%; /* Adjust width as needed */
            padding: 5px;
            margin-bottom: 5px; /* Space between input and buttons */
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; /* Include padding and border in the element's total width and height */
        }
        .actions-cell {
            white-space: nowrap; /* Prevent buttons from wrapping */
        }
    </style>
</head>
<body>

    <nav class="navbar">
        <div class="logo-container">
            <img src="<%= request.getContextPath() %>/scdl.png" alt="Logo"> <span>Symbiosis Center for Distance Learning</span>
        </div>
        <div class="navbar-actions">
            <div class="action-link logout-link">
                <a href="<%= request.getContextPath() %>/LogoutServlet">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <h2>Today's Appointments</h2>

        <%
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            List<String[]> appointments = new ArrayList<>(); // Stores main appointment data
            // Map to store in_out_time status for each appointment:
            // Key: appointmentId, Value: true if in_time is recorded, false otherwise
            java.util.Map<String, Boolean> inTimeRecordedStatus = new java.util.HashMap<>();
            java.util.Map<String, Boolean> outTimeRecordedStatus = new java.util.HashMap<>();


            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            String errorMessage = null;

            try {
                conn = DBConnect.getConnection();
                String todayDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

                // Fetch main appointments
                // MODIFIED SQL QUERY: Added condition to check for EMP_ID being NULL or empty
               String sql = "SELECT id, name, location, appointment_date, from_time, EMP_ID " +
		             			"FROM Schedule_an_Appointment " +
				             	"WHERE appointment_date = ? " +
             					"AND (status = 'Accepted' OR status = 'Rescheduled') " +
             					"AND (EMP_ID IS NULL OR LTRIM(RTRIM(EMP_ID)) = '') " + // <--- CHANGE THIS LINE
             					"ORDER BY from_time ASC";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, todayDate);
                rs = pstmt.executeQuery();

                List<String> appointmentIds = new ArrayList<>();
                while (rs.next()) {
                    String appointmentId = String.valueOf(rs.getInt("id"));
                    // Double check if EMP_ID is present, though the query should handle it
                    String empId = rs.getString("EMP_ID"); 
                    if (empId == null || empId.trim().isEmpty()) { // Only add if EMP_ID is truly not present
                        appointmentIds.add(appointmentId); // Collect IDs for the next query
                        String name = rs.getString("name");
                        String location = rs.getString("location");
                        LocalDate apptDate = rs.getDate("appointment_date").toLocalDate();
                        String appointmentDateFormatted = apptDate.format(dateFormatter);
                        LocalTime fromTime = rs.getTime("from_time").toLocalTime();
                        String fromTimeFormatted = fromTime.format(timeFormatter);

                        appointments.add(new String[]{appointmentId, name, location, appointmentDateFormatted, fromTimeFormatted});
                    }
                }
                rs.close();
                pstmt.close();

                // Fetch in_out_time status for these appointments
                if (!appointmentIds.isEmpty()) {
                    // This part remains mostly the same as it queries the in_out_time table
                    String inOutTimeSql = "SELECT appointment_id, in_time, out_time FROM in_out_time WHERE appointment_id IN (";
                    for (int i = 0; i < appointmentIds.size(); i++) {
                        inOutTimeSql += "?";
                        if (i < appointmentIds.size() - 1) {
                            inOutTimeSql += ",";
                        }
                    }
                    inOutTimeSql += ")";
                    
                    pstmt = conn.prepareStatement(inOutTimeSql);
                    for (int i = 0; i < appointmentIds.size(); i++) {
                        pstmt.setInt(i + 1, Integer.parseInt(appointmentIds.get(i)));
                    }
                    rs = pstmt.executeQuery();

                    while (rs.next()) {
                        String apptId = String.valueOf(rs.getInt("appointment_id"));
                        // If in_time is NOT NULL, it means in-time is recorded
                        inTimeRecordedStatus.put(apptId, rs.getTimestamp("in_time") != null);
                        // If out_time is NOT NULL, it means out-time is recorded
                        outTimeRecordedStatus.put(apptId, rs.getTimestamp("out_time") != null);
                    }
                }

            } catch (SQLException e) {
                System.err.println("Database error retrieving security appointments: " + e.getMessage());
                e.printStackTrace();
                errorMessage = "A database error occurred while retrieving appointments: " + e.getMessage();
            } catch (Exception e) {
                System.err.println("Error retrieving security appointments: " + e.getMessage());
                e.printStackTrace();
                errorMessage = "An unexpected error occurred: " + e.getMessage();
            } finally {
                if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }

            if (errorMessage != null) {
        %>
            <p class="error-message"><%= errorMessage %></p>
        <%
            }

            if (appointments.isEmpty() && errorMessage == null) {
        %>
            <p class="no-appointments">No external visitor appointments scheduled for today with 'Accepted' or 'Rescheduled' status.</p>
        <%
            } else if (!appointments.isEmpty()) {
        %>
            <table class="appointments-table">
                <thead>
                    <tr>
                        <th class="sr-no-col">Sr. No.</th>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Location</th>
                        <th>Date</th>
                        <th>Appointment Time</th>
                        <th>Get Pass No.</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        int srNo = 1;
                        for (String[] appointment : appointments) {
                            String currentAppointmentId = appointment[0];
                            // Check if in_time or out_time has been recorded for this appointment
                            boolean isInTimeDone = inTimeRecordedStatus.getOrDefault(currentAppointmentId, false);
                            boolean isOutTimeDone = outTimeRecordedStatus.getOrDefault(currentAppointmentId, false);
                    %>
                        <tr>
                            <td class="sr-no-col" data-label="Sr. No."><%= srNo++ %></td>
                            <td data-label="Appointment ID"><%= currentAppointmentId %></td>
                            <td data-label="Name"><%= appointment[1] %></td>
                            <td data-label="Location"><%= appointment[2] %></td>
                            <td data-label="Date"><%= appointment[3] %></td>
                            <td data-label="Appointment Time"><%= appointment[4] %></td>
                            
                            <td data-label="Get Pass No.">
                                <%-- Disable the input if in-time is already recorded --%>
                                <input type="text" class="get-pass-input" 
                                        id="getPassNo_<%= currentAppointmentId %>" 
                                        name="getPassNo" 
                                        placeholder="Enter Pass No."
                                        <%= isInTimeDone ? "disabled" : "" %> >
                            </td>

                            <td class="actions-cell" data-label="Actions">
                                <form action="<%= request.getContextPath() %>/T1/RecordTimeServlet" method="post" onsubmit="return validateAndSubmitInTime(this, '<%= currentAppointmentId %>');">
                                    <input type="hidden" name="appointmentId" value="<%= currentAppointmentId %>">
                                    <input type="hidden" name="actionType" value="In-Time">
                                    <input type="hidden" name="getPassNoForInTime" id="hiddenGetPassNo_<%= currentAppointmentId %>">
                                    <%-- Disable the In-Time button if in_time is already recorded --%>
                                    <input type="submit" value="In-Time" id="inTimeBtn_<%= currentAppointmentId %>" <%= isInTimeDone ? "disabled" : "" %>>
                                </form>
                                <form action="<%= request.getContextPath() %>/T1/RecordTimeServlet" method="post" onsubmit="return validateAndSubmitOutTime(this, '<%= currentAppointmentId %>');">
                                    <input type="hidden" name="appointmentId" value="<%= currentAppointmentId %>">
                                    <input type="hidden" name="actionType" value="Out-Time">
                                    <%-- Disable Out-Time button if In-Time isn't done or Out-Time is already done --%>
                                    <input type="submit" value="Out-Time" class="out-time-btn" id="outTimeBtn_<%= currentAppointmentId %>" <%= (!isInTimeDone || isOutTimeDone) ? "disabled" : "" %>>
                                </form>
                            </td>
                        </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        <%
            }
        %>
    </div>

    <script>
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            const successMessage = urlParams.get('success');
            const errorMessage = urlParams.get('error');

            if (successMessage) {
                alert(decodeURIComponent(successMessage));
                // Remove the success parameter from the URL to prevent re-alerting on refresh
                history.replaceState(null, '', window.location.pathname);
            } else if (errorMessage) {
                alert(decodeURIComponent(errorMessage));
                // Remove the error parameter from the URL to prevent re-alerting on refresh
                history.replaceState(null, '', window.location.pathname);
            }
        };

        function validateAndSubmitInTime(form, appointmentId) {
            const getPassNoInput = document.getElementById('getPassNo_' + appointmentId);
            const hiddenGetPassNo = document.getElementById('hiddenGetPassNo_' + appointmentId);
            const inTimeButton = document.getElementById('inTimeBtn_' + appointmentId);
            const outTimeButton = document.getElementById('outTimeBtn_' + appointmentId);

            if (getPassNoInput.value.trim() === '') {
                alert('Please enter a Get Pass No. for In-Time action.');
                getPassNoInput.focus();
                return false; // Prevent form submission
            }
            
            // Set the value of the hidden input field
            hiddenGetPassNo.value = getPassNoInput.value.trim();

            // Disable the "Get Pass No." input and "In-Time" button immediately on valid submission
            // Note: This is client-side visual disabling. The server-side validation is crucial.
            getPassNoInput.disabled = true;
            inTimeButton.disabled = true;
            outTimeButton.disabled = false; // Enable out-time button visually

            return true; // Allow form submission
        }

        function validateAndSubmitOutTime(form, appointmentId) {
            const outTimeButton = document.getElementById('outTimeBtn_' + appointmentId);
            const inTimeButton = document.getElementById('inTimeBtn_' + appointmentId); 
            const getPassNoInput = document.getElementById('getPassNo_' + appointmentId); 

            // Disable Out-Time button on submission
            // Note: This is client-side visual disabling. The server-side validation is crucial.
            outTimeButton.disabled = true;
            // Ensure In-Time button and Get Pass No. input remain disabled visually
            inTimeButton.disabled = true;
            getPassNoInput.disabled = true; 

            return true; // Allow form submission
        }
    </script>
</body>
</html>