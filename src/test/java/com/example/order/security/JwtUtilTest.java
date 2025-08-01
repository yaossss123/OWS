package com.example.order.security;

import com.example.order.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=testSecretKeyForJwtUtilTest2024",
    "jwt.expiration=3600000",
    "jwt.refresh-expiration=86400000"
})
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                ))
                .build();
    }

    @Test
    void testGenerateAccessToken() {
        String token = jwtUtil.generateAccessToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtUtil.generateRefreshToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateAccessToken(userDetails);
        
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testIsValidTokenFormat() {
        String token = jwtUtil.generateAccessToken(userDetails);
        
        assertTrue(jwtUtil.isValidTokenFormat(token));
        assertFalse(jwtUtil.isValidTokenFormat("invalid-token"));
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateAccessToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    void testGetTokenRemainingTime() {
        String token = jwtUtil.generateAccessToken(userDetails);
        Long remainingTime = jwtUtil.getTokenRemainingTime(token);
        
        assertNotNull(remainingTime);
        assertTrue(remainingTime > 0);
    }
} 