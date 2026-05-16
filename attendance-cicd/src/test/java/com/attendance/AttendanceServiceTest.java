package com.attendance;

import com.attendance.model.AttendanceRecord;
import com.attendance.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Attendance Service Unit Tests
 */
public class AttendanceServiceTest {

    private AttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        attendanceService = new AttendanceService();
        attendanceService.clearRecords();
    }

    @Test
    void testCheckIn() {
        AttendanceRecord record = new AttendanceRecord("USER001", "John Doe", "Office");
        AttendanceRecord result = attendanceService.checkIn(record);

        assertNotNull(result);
        assertEquals("CHECKED_IN_SUCCESSFULLY", result.getStatus());
        assertEquals("USER001", result.getUserId());
    }

    @Test
    void testGetRecordCount() {
        assertEquals(0, attendanceService.getRecordCount());

        attendanceService.checkIn(new AttendanceRecord("USER001", "John", "Office"));
        assertEquals(1, attendanceService.getRecordCount());

        attendanceService.checkIn(new AttendanceRecord("USER002", "Jane", "Remote"));
        assertEquals(2, attendanceService.getRecordCount());
    }

    @Test
    void testGetAllRecords() {
        attendanceService.checkIn(new AttendanceRecord("USER001", "John", "Office"));
        attendanceService.checkIn(new AttendanceRecord("USER002", "Jane", "Remote"));

        assertEquals(2, attendanceService.getAllRecords().size());
    }
}
