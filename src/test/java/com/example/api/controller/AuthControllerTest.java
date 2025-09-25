package com.example.api.controller;

import com.example.api.dto.LoginRequest;
import com.example.api.dto.SignupRequest;
import com.example.api.model.User;
import com.example.api.security.JwtService;
import com.example.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(JwtService.class);
        objectMapper = new ObjectMapper();

        AuthController controller = new AuthController(userService, authenticationManager, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_returnsToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("secret");

        Authentication auth = new UsernamePasswordAuthenticationToken("alice", null, null);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken("alice")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signup_createsUser() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setUsername("userNameTest");
        req.setPassword("password");

        User user = User.builder().id(1L).username("userNameTest").passwordHash("x").role("ROLE_USER").build();
        when(userService.register("userNameTest", "password")).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("userNameTest"));
    }
}
