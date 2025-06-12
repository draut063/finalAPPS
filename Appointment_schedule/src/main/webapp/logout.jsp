<%-- webapp/logout.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Invalidate the current session
    session.invalidate();
    // Redirect to the index.html page
    response.sendRedirect("index.html");
%>