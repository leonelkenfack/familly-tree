package com.famillytree.auth.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;
    private final String details;

    public AuthException(String message) {
        super(message);
        this.errorCode = "AUTH_999";
        this.details = message;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AuthException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AuthException(String message, String errorCode, HttpStatus status) {
        this(message, errorCode, status, message);
    }

    private AuthException(String message, String errorCode, HttpStatus status, String details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    public static AuthException invalidCredentials() {
        return new AuthException("AUTH_004", "Invalid credentials", "Username or password is incorrect");
    }

    public static AuthException userNotFound(String email) {
        return new AuthException("AUTH_001", "User not found", "No user found with email: " + email);
    }

    public static AuthException usernameAlreadyExists(String username) {
        return new AuthException("AUTH_002", "Username already exists", "Username is already taken: " + username);
    }

    public static AuthException emailAlreadyExists(String email) {
        return new AuthException("AUTH_003", "Email already exists", "Email is already registered: " + email);
    }

    public static AuthException invalidToken() {
        return new AuthException(
            "Token invalide ou expiré",
            "AUTH_005",
            HttpStatus.UNAUTHORIZED
        );
    }

    public static AuthException missingToken() {
        return new AuthException(
            "Token d'authentification manquant",
            "AUTH_006",
            HttpStatus.BAD_REQUEST
        );
    }

    public static AuthException invalidTokenFormat() {
        return new AuthException(
            "Format de token invalide. Format attendu: 'Bearer <token>'",
            "AUTH_007",
            HttpStatus.BAD_REQUEST
        );
    }

    public static AuthException invalidRefreshToken() {
        return new AuthException("AUTH_005", "Invalid refresh token", "The provided refresh token is invalid or expired");
    }

    public static AuthException unauthorized() {
        return new AuthException("AUTH_006", "Unauthorized", "You don't have permission to perform this action");
    }
} 