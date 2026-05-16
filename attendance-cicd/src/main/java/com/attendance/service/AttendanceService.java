package com.attendance.service;

import com.attendance.model.AttendanceRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Attendance Service
 * Business logic for attendance management
 */
@Service
public class AttendanceService {

    // In-memory storage for demo purposes
    private final ConcurrentHashMap<String, AttendanceRecord> attendanceStore = new ConcurrentHashMap<>();

    /**
     * Process a user check-in
     * @param record The attendance record
     * @return The processed record with generated ID
     */
    public AttendanceRecord checkIn(AttendanceRecord record) {
        String recordId = record.getUserId() + "-" + System.currentTimeMillis();
        record.setStatus("CHECKED_IN_SUCCESSFULLY");
        attendanceStore.put(recordId, record);
        return record;
    }

    /**
     * Get all attendance records
     * @return List of all records
     */
    public List<AttendanceRecord> getAllRecords() {
        return new ArrayList<>(attendanceStore.values());
    }

    /**
     * Get record count
     * @return Number of records
     */
    public int getRecordCount() {
        return attendanceStore.size();
    }

    /**
     * Clear all records (for testing)
     */
    public void clearRecords() {
        attendanceStore.clear();
    }
}
