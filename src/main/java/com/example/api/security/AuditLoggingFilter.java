package com.example.api.security;

import com.example.api.kafka.AuditEvent;
import com.example.api.kafka.AuditEventProducer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditEventProducer producer;

    public AuditLoggingFilter(AuditEventProducer producer) {
        this.producer = producer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
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
            if (path != null && (
                    path.startsWith("/api/v1/audit") ||
                    path.startsWith("/swagger-ui") ||
                    "/swagger-ui.html".equals(path) ||
                    path.startsWith("/v3/api-docs") ||
                    path.startsWith("/api-docs")
            )) {
                return;
            }
            AuditEvent event = new AuditEvent(
                    request.getMethod(),
                    path,
                    username,
                    String.valueOf(response.getStatus()),
                    Instant.now()
            );
            producer.send(event);
        }
    }
}
