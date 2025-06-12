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

        /* New container for action links */
        .navbar-actions {
            display: flex; /* Use flex to align multiple links */
            align-items: center;
        }

        /* Default styling for all action links within navbar */
        .navbar-actions .action-link a {
            text-decoration: none;
            font-size: 16px;
            padding: 8px 15px; /* Slightly more padding for buttons */
            border: 1px solid; /* Border color will be set by specific link classes */
            border-radius: 5px; /* Slightly more rounded corners */
            transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
            white-space: nowrap;
            font-weight: normal; /* Less bold than title */
        }

        
        .action-link.view-appointments-link a:hover {
            background-color: #007BFF; /* Blue background on hover */
            color: white; /* White text on hover */
            border-color: #007BFF; /* Darker blue border on hover */
             color: #007BFF;
            text-decoration: none;
            font-size: 16px;
            padding: 5px 10px;
            border: 1px solid #007BFF;
            border-radius: 4px;
            transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
            white-space: nowrap
        }

        /* Specific styling for the 'Logout' link */
        .action-link.logout-link a {
            color: #007BFF; /* Red text for logout */
            border-color: #007BFF; /* Red border for logout */
            background-color: transparent;
            margin-right: 60px; /* Add margin to the right side of the logout link for proper view */
        }
        .action-link.logout-link a:hover {
            background-color: #007BFF; /* Red background on hover */
            color: white; /* White text on hover */
            border-color: #c82333; /* Darker red border on hover */
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
            border: 1px solid #ddd;
            border-radius: 4px;
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

        /* Employee details display box */
        .employee-details {
            margin-top: 0px;
            margin-bottom: 20px;
            padding: 15px;
            background-color: #e9f5ff;
            border: 1px solid #cce5ff;
            border-radius: 5px;
            color: #0056b3;
            font-size: 16px;
            line-height: 1.6;
            text-align: left;
        }
        .employee-details p {
        	margin-top:0px;
            margin-bottom: 5px;
        }
        .employee-details p:last-child {
            margin-bottom: 0;
        }
        .employee-details strong {
            color: #004085;
        }

        /* Responsive adjustments */
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
                flex-direction: column;
                align-items: flex-start;
                padding: 10px;
                height: auto;
            }

            .logo-container {
                margin-left: 0;
                justify-content: center;
                width: 100%;
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
            /* Reset specific margins for small screens */
            .action-link.view-appointments-link {
                margin-right: 0;
            }
            .action-link.logout-link {
                margin-right: 0;
            }
            .action-link a {
                display: block;
                padding: 10px 0;
            }
            .employee-details {
                margin-left: auto;
                margin-right: auto;
                width: 100%;
                padding: 10px;
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
           
            <div class="action-link logout-link">
                <a href="LogoutServlet">Logout</a> <%-- This will invalidate the session --%>
            </div>
        </div>
    </nav>

    <div class="form-container">
        <div class="appointment-form">
            <h2>Schedule an Appointment</h2>

            <%
                // Retrieve employee details from the session using the correct attribute names
                String employeeName = (String) session.getAttribute("loggedInEmployeeName");
                String employeeEmail = (String) session.getAttribute("loggedInEmployeeEmail");
                String employeeId = (String) session.getAttribute("loggedInEmployeeId");

                // CRITICAL CHECK: If the user is NOT logged in or session data is missing, redirect them.
                // This prevents direct access to this page without going through the login process.
                if (employeeName == null || employeeEmail == null || employeeId == null) {
                    response.sendRedirect("employee-login.html"); // Redirect to your actual Employee Login page
                    return; // Stop further processing of this JSP
                }
            %>

            <div class="employee-details">
               <!--   <p>You are scheduling this appointment as:</p>  -->
                <p>Name: <strong><%= employeeName %></strong></p>
                <p>Employee ID: <strong><%= employeeId %></strong></p>
                <p>Email: <strong><%= employeeEmail %></strong></p>
            </div>

            <form action="EMPprocessAppointment.jsp" method="post">
            
                <label for="location">Location:</label>
                <select id="location" name="location" required>
                    <option value="">Select Location</option>
                    <option value="SCDL">SCDL</option>
                    <option value="SSPU">SSPU</option>
                </select>

                <%-- Meeting Type and Priority fields are commented out as in your original HTML --%>
                <%-- 
                <label for="meetingType">Meeting Type:</label>
                <select id="meetingType" name="meetingType" required>
                    <option value="">Select Meeting Type</option>
                    <option value="Online">Online</option>
                    <option value="Offline">Offline</option>
                </select>     

                <label for="priority">Priority:</label>
                <select id="priority" name="priority" required>
                    <option value="">Select Priority</option>
                    <option value="High">High</option>
                    <option value="Medium">Medium</option>
                    <option value="Low">Low</option>
                </select>
                --%>

                <label for="personToMeet">Required Attendee:</label>
                <input type="text" id="personToMeet" name="personToMeet">
                
                <label for="requiredAttendeeName">Required Attendee Name (optional, if specific):</label>
                <input type="text" id="requiredAttendeeName" name="requiredAttendeeName" >

                <label for="appointmentDate">Preferred Date:</label>
                <input type="date" id="appointmentDate" name="appointmentDate" required>

                <label for="fromTime">From Time:</label>
                <input type="time" id="fromTime" name="fromTime" required>

                <label for="toTime">To Time:</label>
                <input type="time" id="toTime" name="toTime" required>

                <label for="reason">Meeting Agenda:</label>
                <textarea id="reason" name="reason" rows="4"></textarea>

                <button type="submit">Submit</button>
            </form>
        </div>
    </div>

   <script>
    document.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status');
        const message = urlParams.get('message'); // Retrieve the message parameter

        if (status === 'success') {
            alert(message || 'Appointment submitted successfully!'); // Display the specific message or default
            // Clear the status and message parameters from the URL
            window.history.replaceState({}, document.title, window.location.pathname);
        } else if (status === 'error') {
            alert(message || 'Failed to submit appointment. Please try again.'); // Display the specific message or default
            // Clear the status and message parameters from the URL
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });
</script>

</body>
</html>