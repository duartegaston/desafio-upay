package com.example.api.kafka;

import java.time.Instant;

public class AuditEvent {
    private String method;
    private String path;
    private String username;
    private String status;
    private Instant createdAt;

    public AuditEvent() {}

    public AuditEvent(String method, String path, String username, String status, Instant createdAt) {
        this.method = method;
        this.path = path;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
