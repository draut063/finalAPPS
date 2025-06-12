package com.appointment.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String address;
    private String meetingType;
    private String priority;
    private String requiredAttendee;
    private LocalDate appointmentDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private String meetingAgenda;
    private LocalDateTime createdAt;

    // Constructors
    public Appointment() {
    }

    public Appointment(int id, String name, String email, String phone, String location, String address,
                       String meetingType, String priority, String requiredAttendee, LocalDate appointmentDate,
                       LocalTime fromTime, LocalTime toTime, String meetingAgenda, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.address = address;
        this.meetingType = meetingType;
        this.priority = priority;
        this.requiredAttendee = requiredAttendee;
        this.appointmentDate = appointmentDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.meetingAgenda = meetingAgenda;
        this.createdAt = createdAt;
    }

    // Getters and Setters (as provided by you, assuming all are present)
    public int getId() {
    	return id;
    	}
    
    public void setId(int id) {
    	this.id = id;
    	}
    public String getName()  {
    	return name; 
    	}
    public void setName(String name) {
    	this.name = name; 
    	}
    public String getEmail() {
    	return email;
    	}
    public void setEmail(String email) {
    	this.email = email; 
    	}
    public String getPhone() { 
    	return phone; 
    	}
    public void setPhone(String phone) { 
    	this.phone = phone;
    	}
    public String getLocation() {
    	return location; 
    	}
    public void setLocation(String location) {
    	this.location = location;
    	}
    public String getAddress() { 
    	return address; 
    	}
    public void setAddress(String address) {
    	this.address = address;
    	}
    public String getMeetingType() {
    	return meetingType; 
    	}
    public void setMeetingType(String meetingType) {
    	this.meetingType = meetingType; }
    public String getPriority() {
    	return priority; 
    	}
    public void setPriority(String priority) {
    	this.priority = priority; 
    	}
    public String getRequiredAttendee() { 
    	return requiredAttendee; 
    	}
    public void setRequiredAttendee(String requiredAttendee) {
    	this.requiredAttendee = requiredAttendee;
    	}
    public LocalDate getAppointmentDate() {
    	return appointmentDate; 
    	}
    public void setAppointmentDate(LocalDate appointmentDate) {
    	this.appointmentDate = appointmentDate;
    	}
    public LocalTime getFromTime() {
    	return fromTime; 
    	}
    public void setFromTime(LocalTime fromTime) { 
    	this.fromTime = fromTime;
    	}
    public LocalTime getToTime() { 
    	return toTime;
    	}
    public void setToTime(LocalTime toTime) { 
    	this.toTime = toTime;
    	}
    public String getMeetingAgenda() { 
    	return meetingAgenda;
    	}
    public void setMeetingAgenda(String meetingAgenda) {
    	this.meetingAgenda = meetingAgenda; 
    	}
    public LocalDateTime getCreatedAt() {
    	return createdAt;
    	}
    public void setCreatedAt(LocalDateTime createdAt) {
    	this.createdAt = createdAt; 
    	}

    @Override
    public String toString() {
        return "Appointment [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", location="
                + location + ", address=" + address + ", meetingType=" + meetingType + ", priority=" + priority
                + ", RequiredAttendee=" + requiredAttendee + ", appointmentDate=" + appointmentDate + ", fromTime="
                + fromTime + ", toTime=" + toTime + ", meetingAgenda=" + meetingAgenda + ", createdAt=" + createdAt
                + "]";
    }
}