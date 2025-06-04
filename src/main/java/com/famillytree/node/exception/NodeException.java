package com.famillytree.node.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class NodeException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;
    private final String details;

    public NodeException(String message) {
        super(message);
        this.errorCode = "NODE_999";
        this.details = message;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public NodeException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public NodeException(String message, String errorCode, HttpStatus status) {
        this(message, errorCode, status, message);
    }

    private NodeException(String message, String errorCode, HttpStatus status, String details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }

    public static NodeException notFound(Long id) {
        return new NodeException(
            "Node not found with id: " + id,
            "NODE_001",
            HttpStatus.NOT_FOUND
        );
    }

    public static NodeException unauthorized() {
        return new NodeException(
            "You don't have permission to perform this action",
            "NODE_002",
            HttpStatus.FORBIDDEN
        );
    }

    public static NodeException unauthorized(String details) {
        return new NodeException(
            "You don't have permission to perform this action",
            "NODE_002",
            HttpStatus.FORBIDDEN,
            details
        );
    }

    public static NodeException invalidInput(String details) {
        return new NodeException(
            "Invalid input data",
            "NODE_003",
            HttpStatus.BAD_REQUEST,
            details
        );
    }
} 