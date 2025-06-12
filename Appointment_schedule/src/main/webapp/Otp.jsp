<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>OTP Verification</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* Reset & Base Styles */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            flex-direction: column;
            height: 100vh;
            background-color: #f4f4f4;
        }

        /* Navbar */
        .navbar {
            width: 100%;
            background-color: #f9e9de;
            color: black;
            padding: 15px;
            font-size: 22px;
            font-weight: bold;
            position: fixed;
            top: 0;
            left: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1000;
        }

        .logo-container {
            display: flex;
            align-items: center;
        }

        .logo-container img {
            height: 50px;
            margin-right: 10px;
        }

        /* Layout */
        .main-container {
            display: flex;
            flex: 1;
            margin-top: 80px; /* Adjust for fixed navbar height */
            height: calc(100vh - 80px);
        }

        /* Left Image */
        .image-container {
            flex: 60%;
            background: url('scdl1.png') no-repeat center center; /* Ensure this image path is correct */
            background-size: cover;
            position: relative;
        }

        .image-container::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.4);
        }

        /* Right Side */
        .otp-container {
            flex: 40%;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #ffffff;
            padding: 40px 20px;
        }

        .otp-box {
            width: 100%;
            max-width: 350px;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            background-color: #fff;
            text-align: center;
            z-index: 1;
        }

        .otp-box h2 {
            margin-bottom: 20px;
            color: #333;
        }

        input[type="text"] {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border-radius: 5px;
            border: 1px solid #ccc;
            font-size: 16px;
            text-align: center;
        }

        button {
            width: 100%;
            padding: 12px;
            background-color: #007BFF;
            color: white;
            font-size: 16px;
            font-weight: bold;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 10px;
        }

        button:hover {
            background-color: #5e35b1;
        }

        .resend-link {
            display: block;
            margin-top: 15px;
            color: #007BFF;
            text-decoration: none;
            font-size: 14px;
            cursor: pointer;
        }

        .resend-link:hover {
            text-decoration: underline;
        }

        .error-message {
            color: red;
            margin-bottom: 15px;
            font-size: 0.9em;
        }

        .success-message { /* New style for success messages from ResendOtpServlet */
            color: green;
            margin-bottom: 15px;
            font-size: 0.9em;
        }

        @media (max-width: 768px) {
            .main-container {
                flex-direction: column;
            }

            .image-container {
                height: 200px;
                flex: none;
                background-size: cover;
            }

            .otp-container {
                flex: none;
                padding: 20px;
            }

            .logo-container img {
                height: 40px;
            }
        }
    </style>
</head>
<body>

<nav class="navbar">
    <div class="logo-container">
        <img src="scdl.png" alt="Logo"> <span>Symbiosis Center for Distance Learning</span>
    </div>
</nav>

<div class="main-container">
    <div class="image-container"></div>

    <div class="otp-container">
        <div class="otp-box">
            <h2>Enter OTP</h2>
            <%
                String error = request.getParameter("error");
                String message = request.getParameter("message"); // For success messages from ResendOtpServlet
                if (error != null && !error.isEmpty()) {
            %>
                <p class="error-message"><%= error %></p>
            <%
                } else if (message != null && !message.isEmpty()) {
            %>
                <p class="success-message"><%= message %></p>
            <%
                }
            %>
            <form id="otpForm" action="VerifyOtpServlet" method="post">
                <input type="text" name="otp" maxlength="6" pattern="\d{6}" placeholder="Enter 6-digit OTP" required>
                <button type="submit">Submit OTP</button>
            </form>
            <a href="#" class="resend-link" onclick="resendOTP()">Resend OTP</a>
        </div>
    </div>
</div>

<script>
    function resendOTP() {
        alert("Sending new OTP. Please check your email."); // Inform user immediately
        window.location.href = 'ResendOtpServlet';
    }
</script>

</body>
</html>