package com.example.order.controller;

import com.example.order.dto.UserDTO;
import com.example.order.service.UserService;
import com.example.order.util.JwtUtil;
import com.example.order.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证控制器
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户认证", description = "用户认证相关API接口")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并返回访问令牌")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        
        log.info("用户登录，用户名: {}", loginRequest.getUsername());
        
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 生成JWT令牌
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登录成功");
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 3600); // 1小时
            response.put("username", userDetails.getUsername());
            
            log.info("用户登录成功，用户名: {}", userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户登录失败，用户名: {}, 错误信息: {}", loginRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 用户注册
     * 
     * @param userDTO 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        
        log.info("用户注册，用户名: {}", userDTO.getUsername());
        
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            log.info("用户注册成功，用户ID: {}", createdUser.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("用户注册失败，用户名: {}, 错误信息: {}", userDTO.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新访问令牌", description = "使用刷新令牌获取新的访问令牌")
    public ResponseEntity<Map<String, Object>> refreshToken(
            @Parameter(description = "刷新令牌") @RequestParam @NotBlank String refreshToken) {
        
        log.info("刷新访问令牌");
        
        try {
            // 验证刷新令牌
            if (!jwtUtil.isValidTokenFormat(refreshToken)) {
                throw new RuntimeException("无效的刷新令牌");
            }
            
            String username = jwtUtil.extractUsername(refreshToken);
            if (username == null) {
                throw new RuntimeException("无法从刷新令牌中提取用户名");
            }
            
            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 验证刷新令牌
            if (!jwtUtil.validateToken(refreshToken, userDetails)) {
                throw new RuntimeException("刷新令牌无效或已过期");
            }
            
            // 生成新的访问令牌
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "令牌刷新成功");
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 3600);
            response.put("username", username);
            
            log.info("访问令牌刷新成功，用户名: {}", username);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("访问令牌刷新失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 用户登出
     * 
     * @param token 访问令牌
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出并注销令牌")
    public ResponseEntity<Map<String, Object>> logout(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String token) {
        
        log.info("用户登出");
        
        try {
            // TODO: 实现JWT注销逻辑
            // userService.logout(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登出成功");
            
            log.info("用户登出成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户登出失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 验证令牌有效性
     * 
     * @param token 访问令牌
     * @return 验证结果
     */
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证访问令牌的有效性")
    public ResponseEntity<Map<String, Object>> validateToken(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String token) {
        
        log.info("验证访问令牌");
        
        try {
            // 验证令牌格式
            if (!jwtUtil.isValidTokenFormat(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "令牌格式无效");
                return ResponseEntity.ok(response);
            }
            
            // 提取用户名
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "无法从令牌中提取用户名");
                return ResponseEntity.ok(response);
            }
            
            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (userDetails == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }
            
            // 验证令牌
            boolean isValid = jwtUtil.validateToken(token, userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", isValid ? "令牌有效" : "令牌无效或已过期");
            response.put("username", username);
            response.put("remainingTime", jwtUtil.getTokenRemainingTime(token));
            
            log.info("访问令牌验证完成，用户名: {}, 有效: {}", username, isValid);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("访问令牌验证失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取当前用户信息
     * 
     * @param token 访问令牌
     * @return 用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "根据访问令牌获取当前用户信息")
    public ResponseEntity<UserDTO> getCurrentUser(
            @Parameter(description = "访问令牌") @RequestParam @NotBlank String token) {
        
        log.info("获取当前用户信息");
        
        try {
            // TODO: 实现从JWT获取用户信息逻辑
            // UserDTO currentUser = userService.getCurrentUser(token);
            
            // 模拟返回用户信息
            UserDTO mockUser = new UserDTO();
            mockUser.setId(1L);
            mockUser.setUsername("admin");
            mockUser.setEmail("admin@example.com");
            mockUser.setStatus(com.example.order.entity.User.UserStatus.ACTIVE);
            
            log.info("当前用户信息获取成功");
            
            return ResponseEntity.ok(mockUser);
        } catch (Exception e) {
            log.error("获取当前用户信息失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 修改密码
     * 
     * @param changePasswordRequest 修改密码请求
     * @return 修改结果
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户密码")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        
        log.info("修改用户密码，用户名: {}", changePasswordRequest.getUsername());
        
        try {
            // TODO: 实现密码修改逻辑
            // userService.changePassword(changePasswordRequest.getUsername(), 
            //     changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "密码修改成功");
            
            log.info("用户密码修改成功，用户名: {}", changePasswordRequest.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户密码修改失败，用户名: {}, 错误信息: {}", 
                    changePasswordRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 重置密码
     * 
     * @param resetPasswordRequest 重置密码请求
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "重置用户密码")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        
        log.info("重置用户密码，邮箱: {}", resetPasswordRequest.getEmail());
        
        try {
            // TODO: 实现密码重置逻辑
            // userService.resetPassword(resetPasswordRequest.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "密码重置邮件已发送");
            
            log.info("用户密码重置邮件发送成功，邮箱: {}", resetPasswordRequest.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户密码重置失败，邮箱: {}, 错误信息: {}", 
                    resetPasswordRequest.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 登录请求类
     */
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * 修改密码请求类
     */
    public static class ChangePasswordRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;
        
        @NotBlank(message = "新密码不能为空")
        private String newPassword;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    /**
     * 重置密码请求类
     */
    public static class ResetPasswordRequest {
        @NotBlank(message = "邮箱不能为空")
        private String email;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 