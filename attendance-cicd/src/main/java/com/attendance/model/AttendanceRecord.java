package com.attendance.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Attendance Record Model
 * Represents a single check-in event
 */
public class AttendanceRecord {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "User name is required")
    private String userName;

    @NotNull(message = "Check-in time is required")
    private LocalDateTime checkInTime;

    private String location;
    private String status;

    // Constructors
    public AttendanceRecord() {
        this.checkInTime = LocalDateTime.now();
        this.status = "CHECKED_IN";
    }

    public AttendanceRecord(String userId, String userName, String location) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.location = location;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", checkInTime=" + checkInTime +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';

    }
}
