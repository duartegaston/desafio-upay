package com.example.api.controller;

import com.example.api.model.AuditLog;
import com.example.api.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String method,
            @RequestParam(required = false, name = "path") String pathContains,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        Instant fromTs = parseInstantOrThrow(from);
        Instant toTs = parseInstantOrThrow(to);
    
        Page<AuditLog> result = auditService.search(page, size, method, pathContains, username, status, fromTs, toTs);
    
        Map<String, Object> body = new HashMap<>();
        body.put("items", result.getContent());
        body.put("page", result.getNumber());
        body.put("size", result.getSize());
        body.put("total", result.getTotalElements());
        return ResponseEntity.ok(body);
    }
    

    private Instant parseInstantOrThrow(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Instant.parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use ISO-8601, e.g. 2024-01-01T00:00:00Z");
        }
    }
}
