package com.attendance.controller;

import com.attendance.model.AttendanceRecord;
import com.attendance.model.StatusResponse;
import com.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Attendance REST Controller
 * 
 * Endpoints:
 * GET  /attendance/status   → Health check
 * POST /attendance/checkin  → User check-in
 */
@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.environment:development}")
    private String environment;

    @Value("${spring.application.name:attendance-service}")
    private String serviceName;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * GET /attendance/status
     * Health check endpoint - returns service status
     */
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        String hostName = "unknown";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = "localhost";
        }

        StatusResponse response = new StatusResponse(
            serviceName,
            appVersion,
            "UP",
            environment,
            hostName
        );

        return ResponseEntity.ok(response);
    }

    /**
     * POST /attendance/checkin
     * Simulates a user check-in
     */
    @PostMapping("/checkin")
    public ResponseEntity<Map<String, Object>> checkIn(@Valid @RequestBody AttendanceRecord record) {
        AttendanceRecord processed = attendanceService.checkIn(record);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Check-in successful");
        response.put("data", processed);
        response.put("totalRecords", attendanceService.getRecordCount());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /attendance/records
     * Get all attendance records (for verification)
     */
    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getAllRecords() {
        Map<String, Object> response = new HashMap<>();
        response.put("records", attendanceService.getAllRecords());
        response.put("count", attendanceService.getRecordCount());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /attendance/health
     * Simple health endpoint for load balancer
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("service", serviceName);
        return ResponseEntity.ok(health);
    }
}
