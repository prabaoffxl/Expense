package com.expensejar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    public ErrorResponseDTO() {}

    public ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path, Map<String, String> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public void setFieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; }

    public static ErrorResponseDTOBuilder builder() { return new ErrorResponseDTOBuilder(); }

    public static class ErrorResponseDTOBuilder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> fieldErrors;

        public ErrorResponseDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public ErrorResponseDTOBuilder status(int status) { this.status = status; return this; }
        public ErrorResponseDTOBuilder error(String error) { this.error = error; return this; }
        public ErrorResponseDTOBuilder message(String message) { this.message = message; return this; }
        public ErrorResponseDTOBuilder path(String path) { this.path = path; return this; }
        public ErrorResponseDTOBuilder fieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; return this; }

        public ErrorResponseDTO build() {
            return new ErrorResponseDTO(timestamp, status, error, message, path, fieldErrors);
        }
    }
}
