package org.iotp.server.exception;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class IoTPErrorResponse {
    // HTTP Response Status Code
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final IoTPErrorCode errorCode;

    private final Date timestamp;

    protected IoTPErrorResponse(final String message, final IoTPErrorCode errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = new java.util.Date();
    }

    public static IoTPErrorResponse of(final String message, final IoTPErrorCode errorCode, HttpStatus status) {
        return new IoTPErrorResponse(message, errorCode, status);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public IoTPErrorCode getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
