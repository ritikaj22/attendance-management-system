package com.attendance;

import com.attendance.model.AttendanceRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Attendance Controller Integration Tests
 * Tests all REST endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Reset state before each test
    }

    @Test
    void testGetStatus() throws Exception {
        mockMvc.perform(get("/attendance/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("attendance-service"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/attendance/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"));
    }

    @Test
    void testCheckInSuccess() throws Exception {
        AttendanceRecord record = new AttendanceRecord();
        record.setUserId("USER001");
        record.setUserName("John Doe");
        record.setLocation("Building A - Floor 3");

        mockMvc.perform(post("/attendance/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Check-in successful"))
                .andExpect(jsonPath("$.data.userId").value("USER001"))
                .andExpect(jsonPath("$.data.status").value("CHECKED_IN_SUCCESSFULLY"));
    }

    @Test
    void testCheckInValidationError() throws Exception {
        AttendanceRecord record = new AttendanceRecord();
        record.setUserId(""); // Empty userId should fail validation
        record.setUserName("John Doe");

        mockMvc.perform(post("/attendance/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRecords() throws Exception {
        mockMvc.perform(get("/attendance/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists());
    }
}
