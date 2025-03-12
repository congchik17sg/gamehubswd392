package com.example.GameHub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    KEY_INVALID(1002, "Invalid key", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1002, "Username at least 8 characters", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User Existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User Not Existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "you do not have permission", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(1006, "User not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_VERIFIED(1007, "Email is not verified", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "Email existed", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(1009, "Wrong password", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1010, "Wrong OTP", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1008, "Email not existed", HttpStatus.BAD_REQUEST),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
