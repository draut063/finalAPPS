<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Appointment</title>
    <style>
        /* Base Styles */
        body {
            font-family: sans-serif;
            padding: 20px;
            background-color: #f8f8f8;
            margin: 0;
        }

        /* Navbar Styling (Consistent with viewAppointments.jsp) */
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
            justify-content: space-between; /* Space out elements evenly */
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            z-index: 1000;
        }

        .logo-container {
            display: flex;
            margin-left:15px;
            align-items: center;
        }

        .logo-container img {
            height: 50px;
            margin-right: 10px;
        }

        /* New container for action links (Consistent with viewAppointments.jsp) */
        .navbar-actions {
            display: flex; /* Use flex to align multiple links */
            align-items: center;
        }

        /* Styling for all action links within navbar (Consistent with viewAppointments.jsp) */
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

        /* Specific spacing for View Appointments link (Consistent with viewAppointments.jsp's schedule-link) */
        .action-link.view-appointments-link { /* Renamed class for clarity, now consistent */
            margin-right: 15px; /* Space between View Appointments and Logout */
        }

        /* Specific styling for the 'Logout' link (Consistent with viewAppointments.jsp) */
        .action-link.logout-link {
            margin-right: 40px; /* Add margin to the right side of the logout link for proper view */
        }

        /* Space between navbar & form */
        .form-container {
            margin-top: 90px;
        }

        .appointment-form {
            max-width: 500px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .appointment-form h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            margin-top: 10px;
            display: block;
            font-weight: bold;
        }

        input, select, textarea, button {
            display: block;
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            box-sizing: border-box;
        }

        textarea {
            resize: vertical;
        }

        button {
            background-color: #007BFF;
            color: white;
            border: none;
            border-radius: 4px;
            margin-top: 20px;
            font-size: 16px;
            cursor: pointer;
        }

        button:hover {
            background-color: #5e35b1;
        }

        /* Responsive adjustments (Consistent with viewAppointments.jsp) */
        @media (max-width: 768px) {
            body {
                padding: 10px;
            }
            .form-container {
                margin-top: 100px;
            }

            .appointment-form {
                width: 90%;
                padding: 20px;
            }

            /* Responsive Navbar for smaller screens */
            .navbar {
                flex-direction: column; /* Stack items vertically */
                align-items: flex-start; /* Align items to the start */
                padding: 10px;
                height: auto;
            }

            .logo-container {
                margin-bottom: 10px; /* Space between logo and links */
                margin-left: 0; /* Reset margin on small screens */
            }

            .navbar-actions {
                flex-direction: column; /* Stack links vertically */
                width: 100%;
            }
            .action-link {
                width: 100%;
                text-align: center;
                margin-bottom: 10px; /* Space between stacked links */
            }
            .action-link:last-child {
                margin-bottom: 0; /* No margin after the last link */
            }
            /* Reset specific margins for small screens */
            .action-link.view-appointments-link {
                margin-right: 0;
            }
            .action-link.logout-link {
                margin-right: 0;
            }
            .action-link a {
                display: block; /* Make links full width */
                border: 1px solid #007BFF; /* Keep border on small screens */
                padding: 10px 0; /* Adjust padding */
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
                <a href="viewAppointments.jsp">View Appointments</a>
            </div>
            <div class="action-link logout-link">
                <a href="LogoutServlet">Logout</a>
            </div>
        </div>
    </nav>

    <div class="form-container">
        <div class="appointment-form">
            <h2>Schedule an Appointment</h2>
            <form action="processAppointment.jsp" method="post">
            
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>

                <label for="phone">Phone Number:</label>
                <input type="text" id="phone" name="phone">

                <label for="location">Location:</label>
                <select id="location" name="location" required>
                    <option value="">Select Location</option>
                    <option value="SCDL">SCDL</option>
                    <option value="SSPU">SSPU</option>
                </select>

                <label for="address">Address:</label>
                <input type="text" id="address" name="address">

                <label for="meetingType">Meeting Type:</label>
                <select id="meetingType" name="meetingType" required>
                    <option value="">Select Meeting Type</option>
                    <option value="Offline">Offline</option> <%-- Changed to match DB column "In-person" --%>
                    <option value="Online">Online</option> <%-- Changed to match DB column "Online" --%>
                </select>

                <label for="priority">Priority:</label>
                <select id="priority" name="priority" required>
                    <option value="">Select Priority</option>
                    <option value="High">High</option>   <%-- Changed to match DB column "High" --%>
                    <option value="Medium">Medium</option> <%-- Changed to match DB column "Medium" --%>
                    <option value="Low">Low</option>     <%-- Changed to match DB column "Low" --%>
                </select>

                <label for="personToMeet">Required Attendee:</label>
                <input type="text" id="personToMeet" name="personToMeet">
                
                 <label for="personToMeet">Required Attendee Name:</label>
                <input type="text" id="personToMeet" name="personToMeet">

                <label for="appointmentDate">Preferred Date:</label>
                <input type="date" id="appointmentDate" name="appointmentDate" required>

                <label for="fromTime">From Time:</label> <%-- Added hint for user --%>
                <input type="time" id="fromTime" name="fromTime" required>

                <label for="toTime">To Time:</label> <%-- Added hint for user --%>
                <input type="time" id="toTime" name="toTime" required>

                <label for="reason">Meeting Agenda:</label>
                <textarea id="reason" name="reason" rows="4"></textarea>

                <button type="submit">Submit</button>
            </form>
        </div>
    </div>

   <script>
    // JavaScript to check for success parameter and display alert
    document.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status');
        const message = urlParams.get('message'); // Get the message parameter

        if (status === 'success') {
            alert(message || 'Appointment submitted successfully!'); // Display specific message or default
            // Remove the 'status' and 'message' parameters from the URL to avoid re-triggering alert on refresh
            window.location.href = window.location.origin + window.location.pathname;
        } else if (status === 'error') {
            alert(message || 'Failed to submit appointment. Please try again.'); // Display specific message or default
            // Remove the 'status' and 'message' parameters from the URL
            window.location.href = window.location.origin + window.location.pathname;
        }
    });
</script>

</body>
</html>