package com.example.order.controller;

import com.example.order.dto.LoginRequest;
import com.example.order.service.UserService;
import com.example.order.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // 设置登录请求
        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        // 设置用户详情
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa")
                .roles("ADMIN")
                .build();
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNzI5NjAwMCwiZXhwIjoxNjE3MzgwMDAwfQ.test";
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNzI5NjAwMCwiZXhwIjoxNjE3MzgwMDAwfQ.refresh";

        when(authenticationManager.authenticate(any())).thenReturn(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()));
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateAccessToken(userDetails);
        verify(jwtUtil).generateRefreshToken(userDetails);
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("用户名或密码错误"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        verify(authenticationManager).authenticate(any());
        verify(jwtUtil, never()).generateAccessToken(any());
        verify(jwtUtil, never()).generateRefreshToken(any());
    }

    @Test
    void testLogin_InvalidRequest() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest();
        // 不设置必要字段

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNzI5NjAwMCwiZXhwIjoxNjE3MzgwMDAwfQ.refresh";
        String newAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNzI5NjAwMCwiZXhwIjoxNjE3MzgwMDAwfQ.new";

        when(jwtUtil.isValidTokenFormat(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken(refreshToken, userDetails)).thenReturn(true);
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn(newAccessToken);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("令牌刷新成功"))
                .andExpect(jsonPath("$.accessToken").value(newAccessToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(jwtUtil).isValidTokenFormat(refreshToken);
        verify(jwtUtil).extractUsername(refreshToken);
        verify(userDetailsService).loadUserByUsername("admin");
        verify(jwtUtil).validateToken(refreshToken, userDetails);
        verify(jwtUtil).generateAccessToken(userDetails);
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid_token";

        when(jwtUtil.isValidTokenFormat(invalidToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                .param("refreshToken", invalidToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("无效的刷新令牌"));

        verify(jwtUtil).isValidTokenFormat(invalidToken);
        verify(jwtUtil, never()).extractUsername(any());
    }

    @Test
    void testValidateToken_Success() throws Exception {
        // Given
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNzI5NjAwMCwiZXhwIjoxNjE3MzgwMDAwfQ.valid";

        when(jwtUtil.isValidTokenFormat(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);
        when(jwtUtil.getTokenRemainingTime(token)).thenReturn(3500L);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("令牌有效"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.remainingTime").value(3500));

        verify(jwtUtil).isValidTokenFormat(token);
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("admin");
        verify(jwtUtil).validateToken(token, userDetails);
        verify(jwtUtil).getTokenRemainingTime(token);
    }

    @Test
    void testValidateToken_InvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid_token";

        when(jwtUtil.isValidTokenFormat(invalidToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                .param("token", invalidToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("无效的访问令牌"));

        verify(jwtUtil).isValidTokenFormat(invalidToken);
        verify(jwtUtil, never()).extractUsername(any());
    }

    @Test
    void testValidateToken_ExpiredToken() throws Exception {
        // Given
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNzI5NjAwMCwiZXhwIjoxNjE3MzgwMDAwfQ.expired";

        when(jwtUtil.isValidTokenFormat(expiredToken)).thenReturn(true);
        when(jwtUtil.extractUsername(expiredToken)).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken(expiredToken, userDetails)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                .param("token", expiredToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("令牌无效或已过期"));

        verify(jwtUtil).isValidTokenFormat(expiredToken);
        verify(jwtUtil).extractUsername(expiredToken);
        verify(userDetailsService).loadUserByUsername("admin");
        verify(jwtUtil).validateToken(expiredToken, userDetails);
    }
} 