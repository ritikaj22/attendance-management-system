package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Attendance Management System - Main Application Entry Point
 * 
 * Features:
 * - GET /attendance/status  → Health check endpoint
 * - POST /attendance/checkin → User check-in simulation
 * 
 * @author DevOps Team
 * @version 1.0.0
 */
@SpringBootApplication
public class AttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }
}
