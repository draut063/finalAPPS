<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.appointment.schedule.DBConnect" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Appointments</title>
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
            width: 100vw; /* Changed from 100% to 100vw for full viewport width */
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
            justify-content: space-between; /* Space out elements evenly */
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            z-index: 1000;
        }

        .logo-container {
            margin-left:15px;
            display: flex;
            align-items: center;
        }

        .logo-container img {
            height: 50px;
            margin-right: 10px;
        }

        /* New container for action links */
        .navbar-actions {
            display: flex; /* Use flex to align multiple links */
            align-items: center;
        }

        /* Styling for all action links within navbar */
        .navbar-actions .action-link a {
            color: #007BFF;
            text-decoration: none;
            font-size: 16px; /* Normal font size for both */
            padding: 5px 10px; /* Consistent padding for border */
            border: 1px solid #007BFF; /* Small border */
            border-radius: 4px; /* Slightly rounded corners */
            transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
            white-space: nowrap; /* Prevents text from wrapping on smaller screens */
        }


        .navbar-actions .action-link a:hover {
            background-color: #007BFF;

            color: white;
            border-color: #0056b3; /* Darker border on hover */
        }

        /* Specific styling for the 'Schedule New Appointment' link */
        .action-link.schedule-link {
            margin-right: 25px; /* Increased margin to create more space between it and Logout */
        }

        /* Specific styling for the 'Logout' link */
        .action-link.logout-link {
            margin-right: 45px; /* Increased margin to the right side of the logout link for proper view */
        }

        /* Main Content Area */
        .container {
            margin-top: 90px; /* Adjusted to accommodate fixed navbar */
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        /* Header with Counts */
        .header-with-counts {
            display: flex;
            justify-content: space-between; /* Pushes counts to the right */
            align-items: baseline; /* Aligns text baselines */
            margin-bottom: 25px;
            flex-wrap: wrap; /* Allow wrapping on smaller screens */
        }

        .header-with-counts h2 {
            margin: 0; /* Remove default margin from h2 */
            color: #333;
            font-size: 28px;
            text-align: left; /* Align to left within its flex item */
            margin-left: 15px; /* ADDED: Margin from the left side */
        }

        .status-summary {
            font-size: 0.95em;
            color: #555;
            text-align: right; /* Align to right within its flex item */
            white-space: nowrap; /* Prevent wrapping of status counts */
            margin-left: 20px; /* Space between heading and counts */
            display: flex; /* Use flexbox to align count links */
            gap: 15px; /* Space between each status filter link */
        }

        .status-summary a {
            text-decoration: none; /* Remove underline from links */
            color: inherit; /* Inherit color from parent */
            display: flex; /* Make the link a flex container */
            align-items: center; /* Vertically align text and span */
            gap: 5px; /* Space between the text (e.g., "All:") and the count span */
        }

        .status-summary span {
            font-weight: bold;
            padding: 4px 10px; /* Increased padding for better spacing around the number */
            border-radius: 4px;
            display: inline-block; /* To allow padding and border */
            cursor: pointer; /* Indicate clickability */
            transition: background-color 0.2s ease, border-color 0.2s ease;
        }
        .status-summary span:hover {
            opacity: 0.8;
        }

        /* UPDATED: Pending Count Color */
        .status-summary span.pending-count {
            background-color: #cfe2ff; /* Light blue */
            border: 1px solid #0d6efd;
            color: #052c65; /* Darker blue */
        }

        .status-summary span.accepted-count {
            background-color: #d4edda; /* Light green for accepted */
            border: 1px solid #28a745;
            color: #155724;
        }

        .status-summary span.rejected-count {
            background-color: #f8d7da; /* Light red for rejected */
            border: 1px solid #dc3545;
            color: #721c24;
        }

        /* UPDATED: Rescheduled Count Color */
        .status-summary span.rescheduled-count {
            background-color: #fff3cd; /* Light yellow */
            border: 1px solid #ffc107;
            color: #856404; /* Dark yellow/orange */
        }

        /* NEW: All Appointments count style */
        .status-summary span.all-count {
            background-color: #e2e3e5; /* Light grey */
            border: 1px solid #6c757d;
            color: #343a40; /* Dark grey */
        }

        /* NEW: Active filter highlighting */
        .status-summary span.active-filter {
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.5); /* Blue glow */
            border-color: #007BFF;
            transform: translateY(-1px); /* Slight lift */
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

        /* Action Links in Table */
        .appointments-table .action-link-group {
            display: flex;
            gap: 10px; /* Space between links */
            flex-wrap: wrap; /* Allow wrapping on smaller screens */
            align-items: center;
            
        }

        .appointments-table .action-link-group a {
            text-decoration: none;
            font-weight: bold;
            transition: color 0.2s ease;
            white-space: nowrap; /* Prevent breaking action links */
            margin-top:17px;
        }

        .appointments-table .edit-link {
            color: #007BFF;
        }
        .appointments-table .edit-link:hover {
            color: #0056b3;
            text-decoration: underline;
        }
        .appointments-table .delete-link {
            color: #DC3545; /* Red color for delete */
        }
        .appointments-table .delete-link:hover {
            color: #c82333; /* Darker red on hover */
            text-decoration: underline;
        }

        /* Status display in table */
        .appointments-table .status-display {
            font-weight: bold;
            /* No padding or border for just text status in the table */
        }

        /* UPDATED COLORS FOR TABLE STATUS DISPLAY */
        .appointments-table .status-display.pending {
            color: #007bff; /* Blue for Pending */
        }

        .appointments-table .status-display.accepted {
        	margin-top:17px;
            color: #28a745; /* Green for Accepted */
        }

        .appointments-table .status-display.rejected {
        	margin-top:17px;
            color: #dc3545; /* Red for Rejected */
        }

        .appointments-table .status-display.rescheduled {
        	margin-top:17px;
            color: #ffc107; /* Yellow for Rescheduled */
        }

        /* In/Out status colors remain the same as they were not part of the request */
        .appointments-table .status-display.in {
            color: #007bff; /* Blue for 'In' */
        }

        .appointments-table .status-display.out {
            color: #6c757d; /* Grey for 'Out' */
        }


        /* --- NEW/UPDATED STYLE FOR SR. NO. COLUMN --- */
        .appointments-table th.sr-no-col,
        .appointments-table td.sr-no-col {
            width: 5%; /* Adjust width as needed, e.g., 5%, 50px */
            text-align: center; /* Center the number */
        }
        /* --- END OF NEW/UPDATED STYLE --- */

        /* Styling for In/Out column (can contain status or actions) */
        .appointments-table th.in-out-col,
        .appointments-table td.in-out-col {
            width: 15%; /* Adjusted width to fit status/actions comfortably */
            text-align: center; /* Center the text */
        }


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
                position: static; /* Make navbar static on small screens */
            }
            .logo-container {
                margin-bottom: 10px;
            }
            .navbar-actions {
                flex-direction: column; /* Stack links vertically */
                width: 100%;
            }
            .action-link { /* Targets individual link containers in navbar */
                width: 100%;
                text-align: center;
                margin-bottom: 10px; /* Space between stacked links */
            }
            .action-link:last-child {
                margin-bottom: 0; /* No margin after the last link */
            }
            /* Reset specific margins for small screens */
            .action-link.schedule-link {
                margin-right: 0;
            }
            .action-link.logout-link {
                margin-right: 0;
            }
            .action-link a { /* Targets the <a> tag itself */
                display: block; /* Make links full width */
                border: 1px solid #007BFF; /* Keep border on small screens */
                padding: 10px 0; /* Adjust padding */
            }
            .container {
                margin-top: 20px; /* Reset top margin as navbar is static */
                padding: 10px;
            }
            .header-with-counts {
                flex-direction: column; /* Stack heading and counts vertically */
                align-items: flex-start; /* Align all to start */
                gap: 10px; /* Space between stacked items */
            }
            .status-summary {
                flex-direction: column; /* Stack count links vertically */
                align-items: flex-start; /* Align counts to left when stacked */
                margin-left: 0; /* Remove left margin when stacked */
                gap: 10px; /* Adjust gap for stacked links */
            }
            .status-summary a {
                width: 100%; /* Make each link take full width */
                justify-content: space-between; /* Space out text and count within each link */
            }
            /* Adjust h2 margin for small screens */
            .header-with-counts h2 {
                margin-left: 0; /* Remove margin-left when stacked vertically */
            }
            .appointments-table, .appointments-table tbody, .appointments-table tr, .appointments-table td, .appointments-table th {
                display: block;
            }
            .appointments-table thead {
                display: none; /* Hide table header on small screens */
            }
            .appointments-table tr {
                margin-bottom: 15px;
                border: 1px solid #ddd;
                border-radius: 5px;
                overflow: hidden; /* Ensures child elements conform to border-radius */
            }
            .appointments-table td {
                text-align: right;
                padding-left: 50%; /* Create space for the label */
                position: relative;
                border-bottom: 1px solid #eee; /* Separator for stacked cells */
            }
            .appointments-table td:last-child {
                border-bottom: none; /* No border for the last cell in a stacked row */
            }
            .appointments-table td::before {
                content: attr(data-label); /* Get label from data-label attribute */
                position: absolute;
                left: 15px;
                width: calc(50% - 30px); /* Adjust width for label */
                text-align: left;
                font-weight: bold;
                color: #555;
            }
        }
    </style>
</head>
<body>

    <nav class="navbar">
        <div class="logo-container">
            <img src="scdl.png" alt="Logo"> <span>Symbiosis Center for Distance Learning</span>
        </div>
        <div class="navbar-actions">
            <div class="action-link schedule-link">
                <a href="Schedule_an_appointment.jsp">Schedule New Appointment</a>
            </div>
            <div class="action-link logout-link">
                <a href="LogoutServlet">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <%
            // Initialize JDBC resources
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            List<String[]> appointments = new ArrayList<>(); // To store fetched data
            Map<String, Integer> statusCounts = new HashMap<>(); // To store status counts
            statusCounts.put("All", 0); // Add "All" for total count
            statusCounts.put("Pending", 0);
            statusCounts.put("Accepted", 0);
            statusCounts.put("Rejected", 0);
            statusCounts.put("Rescheduled", 0); // Initialize for Rescheduled status

            // Define date and time formatters
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // For 12-hour format with AM/PM

            String errorMessage = null; // For storing database error message

            try {
                conn = DBConnect.getConnection(); // Get connection from your DBConnect utility

                // Get today's date in YYYY-MM-DD format for the SQL query
                String todayDate = LocalDate.now().toString();

                // SQL query: Select required columns from Schedule_an_Appointment and join with In_Out_Time
                // Use LEFT JOIN to ensure all appointments are listed, even if they don't have In/Out times yet
                String sql = "SELECT sa.id, sa.name, sa.email, sa.appointment_date, sa.from_time, sa.to_time, sa.status, " +
                             "iot.in_time, iot.out_time " + // Only need in_time and out_time to determine In/Out status
                             "FROM Schedule_an_Appointment sa " +
                             "LEFT JOIN In_Out_Time iot ON sa.id = iot.appointment_id " +
                             "WHERE sa.appointment_date >= ? " +
                             "ORDER BY sa.appointment_date ASC, sa.from_time ASC";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, todayDate); // Set the current date parameter

                rs = pstmt.executeQuery();

                // Fetch data into the list and update status counts
                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("id"));
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String status = rs.getString("status");

                    // Get appointment_date as LocalDate and format it
                    LocalDate apptDate = rs.getDate("appointment_date").toLocalDate();
                    String appointmentDateFormatted = apptDate.format(dateFormatter);

                    // Get from_time as LocalTime and format it using the 12-hour formatter
                    LocalTime fromTime = rs.getTime("from_time").toLocalTime();
                    String fromTimeFormatted = fromTime.format(timeFormatter);

                    // Get to_time as LocalTime and format it using the 12-hour formatter
                    LocalTime toTime = rs.getTime("to_time").toLocalTime();
                    String toTimeFormatted = toTime.format(timeFormatter);

                    // Retrieve in_time and out_time from In_Out_Time table (can be null)
                    LocalTime inTime = null;
                    try {
                        inTime = rs.getTime("in_time") != null ? rs.getTime("in_time").toLocalTime() : null;
                    } catch (SQLException e) {
                        System.err.println("Warning: in_time column issue - " + e.getMessage());
                    }

                    LocalTime outTime = null;
                    try {
                        outTime = rs.getTime("out_time") != null ? rs.getTime("out_time").toLocalTime() : null;
                    } catch (SQLException e) {
                        System.err.println("Warning: out_time column issue - " + e.getMessage());
                    }

                    // Determine In/Out status for display purposes
                    String displayInOutStatus = "N/A"; // Default if no activity recorded
                    if (inTime != null && outTime == null) {
                        displayInOutStatus = "In"; // Visitor has entered but not left
                    } else if (inTime != null && outTime != null) {
                        displayInOutStatus = "Out"; // Visitor has entered and left
                    }
                    // If inTime is null, it remains "N/A" (or you could assign "Not Checked In")

                    // Store only the necessary data points for the streamlined table
                    // Order: ID, Name, Email, Appointment Date, From Time, To Time, In/Out Status (for column content), Status (for filter/class)
                    appointments.add(new String[]{id, name, email, appointmentDateFormatted, fromTimeFormatted, toTimeFormatted, displayInOutStatus, status});

                    // Update status counts - ensure the key matches the actual status value from DB
                    statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                    statusCounts.put("All", statusCounts.get("All") + 1); // Increment total count
                }

            } catch (SQLException e) {
                // Log the error to console and display a user-friendly message
                System.err.println("Database error retrieving appointments: " + e.getMessage());
                e.printStackTrace();
                errorMessage = "A database error occurred while retrieving appointments: " + e.getMessage();
            } catch (Exception e) { // Catch other potential errors like NullPointerException if DBConnect.getConnection() fails
                System.err.println("Error retrieving appointments: " + e.getMessage());
                e.printStackTrace();
                errorMessage = "An unexpected error occurred: " + e.getMessage();
            }
            finally {
                // Close resources in reverse order of creation
                if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
                if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
                if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
            }

            // Display error message if any
            if (errorMessage != null) {
        %>
            <p class="error-message"><%= errorMessage %></p>
        <%
            }
        %>

        <div class="header-with-counts">
            <h2>Appointments</h2>
            <div class="status-summary">
                <a href="#" class="status-filter" data-filter="All">All: <span class="all-count active-filter"><%= statusCounts.get("All") %></span></a>
                <a href="#" class="status-filter" data-filter="Pending">Pending: <span class="pending-count"><%= statusCounts.get("Pending") %></span></a>
                <a href="#" class="status-filter" data-filter="Accepted">Accepted: <span class="accepted-count"><%= statusCounts.get("Accepted") %></span></a>
                <a href="#" class="status-filter" data-filter="Rejected">Rejected: <span class="rejected-count"><%= statusCounts.get("Rejected") %></span></a>
                <%-- Display Rescheduled count if it exists and is greater than 0 --%>
                <% if (statusCounts.get("Rescheduled") != null && statusCounts.get("Rescheduled") > 0) { %>
                    <a href="#" class="status-filter" data-filter="Rescheduled">Rescheduled: <span class="rescheduled-count"><%= statusCounts.get("Rescheduled") %></span></a>
                <% } %>
            </div>
        </div>

        <%
            // Check if any appointments were found
            if (appointments.isEmpty() && errorMessage == null) { // Only show "no appointments" if there wasn't an error
        %>
            <p class="no-appointments">No current or future appointments scheduled.</p>
        <%
            } else if (!appointments.isEmpty()) { // Only show table if there are appointments
        %>
            <table class="appointments-table" id="appointmentsTable">
                <thead>
                    <tr>
                        <th class="sr-no-col">Sr. No.</th>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Appointment Date</th>
                        <th>From Time</th>
                        <th>To Time</th>
                        <th class="in-out-col">In/Out</th> <%-- Display In/Out status here --%>
                        <th>Action / Status</th> <%-- Action links or appointment status --%>
                    </tr>
                </thead>
                <tbody>
                        <%
                            for (String[] appointment : appointments) {
                                String id = appointment[0];
                                String name = appointment[1];
                                String email = appointment[2];
                                String appointmentDateFormatted = appointment[3];
                                String fromTimeFormatted = appointment[4];
                                String toTimeFormatted = appointment[5];
                                String displayInOutStatus = appointment[6]; // Determined In/Out status
                                String status = appointment[7]; // Actual status for filtering/logic
                        %>
                                <tr data-status="<%= status %>">
                                    <td class="sr-no-col" data-label="Sr. No."></td>
                                    <td data-label="ID"><%= id %></td>
                                    <td data-label="Name"><%= name %></td>
                                    <td data-label="Email"><%= email %></td>
                                    <td data-label="Appointment Date"><%= appointmentDateFormatted %></td>
                                    <td data-label="From Time"><%= fromTimeFormatted %></td>
                                    <td data-label="To Time"><%= toTimeFormatted %></td>
                                    <td class="in-out-col" data-label="In/Out">
                                        <span class="status-display <%= displayInOutStatus.toLowerCase() %>">
                                            <%= displayInOutStatus %>
                                        </span>
                                    </td>
                                    <td data-label="Action / Status" class="action-link-group">
                                        <%
                                            if ("Pending".equals(status)) {
                                        %>
                                                    <a href="editAppointment.jsp?id=<%= id %>" class="edit-link">Edit</a>
                                                    <a href="DeleteAppointmentServlet?id=<%= id %>" class="delete-link" onclick="return confirm('Are you sure you want to delete this appointment?');">Delete</a>
                                        <%
                                            } else {
                                                    // For Accepted, Rejected, Rescheduled, just display the original status text with color
                                        %>
                                                    <span class="status-display <%= status.toLowerCase() %>"><%= status %></span>
                                        <%
                                            }
                                        %>
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
        function updateSerialNumbers() {
            const visibleRows = document.querySelectorAll('#appointmentsTable tbody tr[style*="display: table-row"], #appointmentsTable tbody tr:not([style*="display"])'); // Select visible rows or rows without explicit display style
            let srNo = 1;
            visibleRows.forEach(row => {
                const srNoCell = row.querySelector('.sr-no-col');
                if (srNoCell) {
                    srNoCell.textContent = srNo++;
                }
            });
        }

        document.addEventListener('DOMContentLoaded', function() {
            const filterLinks = document.querySelectorAll('.status-filter');
            const appointmentTableBody = document.querySelector('#appointmentsTable tbody');
            const appointmentRows = appointmentTableBody ? appointmentTableBody.querySelectorAll('tr') : []; // Get all rows initially

            // Initial serial number update
            updateSerialNumbers();

            filterLinks.forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault(); // Prevent default link behavior (page refresh)

                    const filterStatus = this.dataset.filter; // Get the status to filter by

                    // Remove active class from all filter links
                    document.querySelectorAll('.status-summary span').forEach(span => {
                        span.classList.remove('active-filter');
                    });
                    // Add active class to the clicked link's span
                    this.querySelector('span').classList.add('active-filter');

                    let hasVisibleRows = false; // Flag to check if any rows are visible
                    appointmentRows.forEach(row => {
                        const rowStatus = row.dataset.status;
                        if (filterStatus === 'All' || rowStatus === filterStatus) {
                            row.style.display = 'table-row'; // Show the row
                            hasVisibleRows = true;
                        } else {
                            row.style.display = 'none'; // Hide the row
                        }
                    });

                    // Update serial numbers after filtering
                    updateSerialNumbers();

                    // Show/hide "No appointments" message based on visibility
                    const noAppointmentsMessage = document.querySelector('.no-appointments');
                    if (noAppointmentsMessage) { // Check if the message exists
                        if (hasVisibleRows) {
                            noAppointmentsMessage.style.display = 'none';
                        } else {
                            noAppointmentsMessage.style.display = 'block';
                        }
                    }
                });
            });

            // Handle case where there are no appointments initially loaded from JSP
            const initialNoAppointmentsMessage = document.querySelector('.no-appointments');
            if (initialNoAppointmentsMessage && appointmentRows.length > 0) {
                initialNoAppointmentsMessage.style.display = 'none';
            } else if (initialNoAppointmentsMessage && appointmentRows.length === 0) {
                   initialNoAppointmentsMessage.style.display = 'block';
            }
        });
    </script>

</body>
</html>