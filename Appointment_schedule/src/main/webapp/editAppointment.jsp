<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.appointment.schedule.DBConnect" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Appointment</title>
    <style>
        /* Base Styles */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            color: #333;
        }

        /* Navbar Styling (Consistent across all pages now) */
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
        	margin-left:15px;
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

        /* Specific spacing for View Appointments link (consistent with Schedule_an_appointment.jsp's schedule-link) */
        .action-link.view-appointments-link {
            margin-right: 15px; /* Space between View Appointments and Logout */
        }

        /* Specific styling for the 'Logout' link */
        .action-link.logout-link {
            margin-right: 45px; /* Consistent margin from viewAppointments */
        }


        /* Main Content Area / Form Container */
        .container { /* Renamed from .form-container in Schedule_an_appointment for consistency with this page */
            margin-top: 90px; /* Space between navbar & form */
            padding: 20px; /* Kept existing padding */
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 500px; /* Adjusted to 500px for consistency with Schedule_an_appointment's form-container */
            margin-left: auto;
            margin-right: auto;
        }

        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 25px; /* Matches schedule form */
            font-size: 28px; /* Matches schedule form */
        }

        .form-group {
            margin-bottom: 15px; /* Consistent with schedule form */
        }

        .form-group label { /* Adjusted for consistency */
            display: block;
            margin-bottom: 5px;
            margin-top: 10px; /* Added from Schedule_an_appointment.jsp */
            font-weight: bold;
            color: #555;
        }

        .form-group input[type="text"],
        .form-group input[type="email"],
        .form-group input[type="tel"],
        .form-group input[type="date"],
        .form-group input[type="time"],
        .form-group select,
        .form-group textarea {
            width: calc(100% - 22px); /* Account for padding and border */
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; /* Include padding and border in the element's total width and height */
            font-size: 16px;
            margin-top: 5px; /* Added from Schedule_an_appointment.jsp */
        }

        .form-group textarea {
            resize: vertical; /* Allow vertical resizing */
            min-height: 80px;
        }

        .form-group select {
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23007bff%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%00-13%205.4L146.2%20208.7%2018.8%2074.8c-1.8-2.6-4.9-4.8-8.2-4.8s-6.5%202.2-8.2%204.8L.4%2097.7c-2.4%203.4-2.4%208.5%200%2011.9l138%20138c2.4%203.4%207.6%203.4%2010%200l138-138c2.4-3.4%202.4-8.5%200-11.9l-10-14.5c-1.8-2.6-4.9-4.8-8.2-4.8z%22%2F%3E%3C%2Fsvg%3E');
            background-repeat: no-repeat;
            background-position: right 10px center;
            background-size: 12px;
        }

        .button-group {
            text-align: center;
            margin-top: 20px;
        }

        .button-group button {
            padding: 12px 25px;
            background-color: #007BFF; /* Primary button color */
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin: 0 10px;
            transition: background-color 0.3s ease;
        }

        .button-group button[type="reset"] {
            background-color: #6c757d; /* Gray for Reset - consistent */
        }

        .button-group button:hover {
            background-color: #5e35b1; /* Hover color for primary button */
            opacity: 1;
        }

        /* Message Styles */
        .error-message, .success-message {
            text-align: center;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            font-weight: bold;
        }

        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .success-message {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        /* Responsive adjustments */
        @media (max-width: 768px) {
            body {
                padding: 10px;
            }
            .container {
                margin-top: 150px;
                padding: 15px;
                max-width: 90%;
            }
            .button-group button {
                width: 100%;
                margin: 10px 0;
            }
            /* Responsive Navbar for smaller screens */
            .navbar {
                flex-direction: column;
                align-items: flex-start;
                padding: 10px;
                height: auto;
            }

            .logo-container {
                margin-bottom: 10px;
                margin-left: 0;
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
            .action-link.view-appointments-link,
            .action-link.schedule-link {
                margin-right: 0;
            }
            .action-link.logout-link {
                margin-right: 0;
            }
            .action-link a {
                display: block;
                border: 1px solid #007BFF;
                padding: 10px 0;
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
            <div class="action-link view-appointments-link">
                <a href="viewAppointments.jsp">View All Appointments</a>
            </div>
            <div class="action-link logout-link">
                <a href="LogoutServlet">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <h2>Edit Appointment</h2>

        <%
            String appointmentId = request.getParameter("id");
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            // Variables to hold appointment data
            String id = "";
            String name = "";
            String email = "";
            String phone = "";
            String location = "";
            String address = "";
            String meetingType = "";
            String priority = "";
            String requiredAttendee = "";
            String appointmentDate = ""; // For ISO YYYY-MM-dd format required by input type="date"
            String fromTime = "";       // For HH:mm format required by input type="time"
            String toTime = "";         // For HH:mm format required by input type="time"
            String meetingAgenda = "";

            // Check for messages from the servlet (success/error)
            String message = (String) request.getSession().getAttribute("message");
            if (message != null) {
                if (message.contains("successfully")) {
        %>
                    <div class="success-message"><%= message %></div>
        <%
                } else {
        %>
                    <div class="error-message"><%= message %></div>
        <%
                }
                request.getSession().removeAttribute("message"); // Clear message after displaying
            }

            // Fetch appointment data if a valid ID is provided
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
        %>
                <p class="error-message">Invalid Appointment ID provided.</p>
        <%
            } else {
                try {
                    conn = DBConnect.getConnection();
                    String sql = "SELECT * FROM Schedule_an_Appointment WHERE id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, Integer.parseInt(appointmentId));
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        // Populate variables with data from the ResultSet
                        id = String.valueOf(rs.getInt("id"));
                        name = rs.getString("name");
                        email = rs.getString("email");
                        phone = rs.getString("phone");
                        location = rs.getString("location");
                        address = rs.getString("address");
                        meetingType = rs.getString("meeting_type");
                        priority = rs.getString("priority");
                        requiredAttendee = rs.getString("Required_Attendee");

                        // Format Date to ISO YYYY-MM-dd for input type="date"
                        LocalDate apptDate = rs.getDate("appointment_date").toLocalDate();
                        appointmentDate = apptDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        // Format Time to HH:mm for input type="time" (this is the value needed by the input field)
                        LocalTime ft = rs.getTime("from_time").toLocalTime();
                        fromTime = ft.format(DateTimeFormatter.ofPattern("HH:mm"));

                        LocalTime tt = rs.getTime("to_time").toLocalTime();
                        toTime = tt.format(DateTimeFormatter.ofPattern("HH:mm"));

                        meetingAgenda = rs.getString("Meeting_Agenda");
                    } else {
        %>
                        <p class="error-message">Appointment not found for ID: <%= appointmentId %></p>
        <%
                    }

                } catch (NumberFormatException e) {
        %>
                    <p class="error-message">Invalid Appointment ID format. The ID must be a number.</p>
        <%
                } catch (SQLException e) {
                    System.err.println("Database error fetching appointment: " + e.getMessage());
                    e.printStackTrace();
        %>
                    <p class="error-message">A database error occurred while fetching appointment details. Please try again later.</p>
        <%
                } finally {
                    // Close JDBC resources
                    if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        %>

        <form action="UpdateAppointmentServlet" method="post">
            <input type="hidden" name="id" value="<%= id %>">

            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" value="<%= name %>" required>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="<%= email %>" required>
            </div>

            <div class="form-group">
                <label for="phone">Phone:</label>
                <input type="tel" id="phone" name="phone" value="<%= phone %>" maxlength="10">
            </div>

            <div class="form-group">
                <label for="location">Location:</label>
                <input type="text" id="location" name="location" value="<%= location %>" required>
            </div>

            <div class="form-group">
                <label for="address">Address:</label>
                <textarea id="address" name="address"><%= address %></textarea>
            </div>

            <div class="form-group">
                <label for="meeting_type">Meeting Type:</label>
                <select id="meeting_type" name="meeting_type">
                    <option value="Online" <%= "Online".equals(meetingType) ? "selected" : "" %>>Online</option>
                    <option value="In-person" <%= "In-person".equals(meetingType) ? "selected" : "" %>>In-person</option>
                    <option value="Call" <%= "Call".equals(meetingType) ? "selected" : "" %>>Call</option>
                </select>
            </div>

            <div class="form-group">
                <label for="priority">Priority:</label>
                <select id="priority" name="priority">
                    <option value="High" <%= "High".equals(priority) ? "selected" : "" %>>High</option>
                    <option value="Medium" <%= "Medium".equals(priority) ? "selected" : "" %>>Medium</option>
                    <option value="Low" <%= "Low".equals(priority) ? "selected" : "" %>>Low</option>
                </select>
            </div>

            <div class="form-group">
                <label for="required_attendee">Required Attendee:</label>
                <input type="text" id="required_attendee" name="required_attendee" value="<%= requiredAttendee %>">
            </div>

            <div class="form-group">
                <label for="appointment_date">Appointment Date:</label>
                <input type="date" id="appointment_date" name="appointment_date" value="<%= appointmentDate %>" required>
            </div>

            <div class="form-group">
                <label for="from_time">From Time (Browser handles 12-hour display):</label> <%-- Added hint --%>
                <input type="time" id="from_time" name="from_time" value="<%= fromTime %>" required>
            </div>

            <div class="form-group">
                <label for="to_time">To Time (Browser handles 12-hour display):</label> <%-- Added hint --%>
                <input type="time" id="to_time" name="to_time" value="<%= toTime %>" required>
            </div>

            <div class="form-group">
                <label for="meeting_agenda">Meeting Agenda:</label>
                <textarea id="meeting_agenda" name="meeting_agenda"><%= meetingAgenda %></textarea>
            </div>

            <div class="button-group">
                <button type="submit">Update Appointment</button>
                <button type="reset">Reset</button>
            </div>
        </form>
    </div>

</body>
</html>