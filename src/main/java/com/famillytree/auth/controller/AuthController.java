package com.famillytree.auth.controller;

import com.famillytree.auth.dto.AuthRequest;
import com.famillytree.auth.dto.AuthResponse;
import com.famillytree.auth.dto.RefreshTokenRequest;
import com.famillytree.auth.dto.RegisterRequest;
import com.famillytree.auth.exception.AuthException;
import com.famillytree.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User registration details") @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns JWT tokens")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "User login credentials") @Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generates a new access token using a refresh token")
    public ResponseEntity<AuthResponse> refreshToken(
            @Parameter(description = "Refresh token request") @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify token", description = "Verifies if the provided JWT token is valid")
    public ResponseEntity<Map<String, Boolean>> verifyToken(
            @Parameter(description = "Authorization header with Bearer token") 
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw AuthException.invalidTokenFormat();
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok(Map.of("valid", authService.verifyToken(token)));
    }
} 