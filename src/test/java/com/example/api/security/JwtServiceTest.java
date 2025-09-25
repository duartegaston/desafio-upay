package com.example.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // fake base64 secret (matches docker-compose default format: base64-encoded)
        String base64Secret = "ZmFrZV9kZWNvZGVkX2Jhc2U2NF9zZWNyZXRfa2V5X3RvX3Rlc3Q=";
        ReflectionTestUtils.setField(jwtService, "secret", base64Secret);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generateAndExtractUsername_shouldWork() {
        String token = jwtService.generateToken("userNameTest");
        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals("userNameTest", username);
    }
}
