package com.example.api.security;

import com.example.api.model.AuditLog;
import com.example.api.repository.AuditLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditLogRepository auditLogRepository;

    public AuditLoggingFilter(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Prefer username explicitly provided by controller (e.g., on signup/login)
            String username = null;
            Object auditUsernameAttr = request.getAttribute("auditUsername");
            if (auditUsernameAttr instanceof String s && !s.isBlank()) {
                username = s;
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    username = auth.getName();
                }
            }
            String path = request.getRequestURI();
            if (path != null && path.startsWith("/api/v1/audit")) {
                return;
            }
            AuditLog logEntry = AuditLog.builder()
                    .method(request.getMethod())
                    .path(path)
                    .username(username)
                    .status(String.valueOf(response.getStatus()))
                    .build();
            auditLogRepository.save(logEntry);
        }
    }
}
