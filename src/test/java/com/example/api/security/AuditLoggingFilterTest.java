package com.example.api.security;

import com.example.api.kafka.AuditEvent;
import com.example.api.kafka.AuditEventProducer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuditLoggingFilterTest {

    private AuditEventProducer producer;
    private AuditLoggingFilter filter;

    @BeforeEach
    void setUp() {
        producer = mock(AuditEventProducer.class);
        filter = new AuditLoggingFilter(producer);
    }

    @Test
    void auditablePath_sendsEvent_withAttributeUsername() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setAttribute("auditUsername", "userNameTest");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> ((HttpServletResponse) res).setStatus(200);

        filter.doFilter(request, response, chain);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(producer, times(1)).send(captor.capture());
        AuditEvent event = captor.getValue();
        assertEquals("POST", event.getMethod());
        assertEquals("/api/v1/auth/login", event.getPath());
        assertEquals("userNameTest", event.getUsername());
        assertEquals("200", event.getStatus());
    }

    @Test
    void nonAuditablePath_doesNotSendEvent() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> ((HttpServletResponse) res).setStatus(200);

        filter.doFilter(request, response, chain);

        verify(producer, never()).send(any());
    }
}
