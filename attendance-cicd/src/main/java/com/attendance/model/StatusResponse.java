package com.attendance.model;

import java.time.LocalDateTime;

/**
 * Service Status Response Model
 * Provides health check information
 */
public class StatusResponse {

    private String serviceName;
    private String version;
    private String status;
    private LocalDateTime timestamp;
    private String environment;
    private String hostName;

    public StatusResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public StatusResponse(String serviceName, String version, String status, 
                          String environment, String hostName) {
        this();
        this.serviceName = serviceName;
        this.version = version;
        this.status = status;
        this.environment = environment;
        this.hostName = hostName;
    }

    // Getters and Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
}
