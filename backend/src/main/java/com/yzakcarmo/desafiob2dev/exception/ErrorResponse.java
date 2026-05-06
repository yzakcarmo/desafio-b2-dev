package com.yzakcarmo.desafiob2dev.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private List<String> details;
    private String traceId;
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public ErrorResponse() {}

    public ErrorResponse(int status, String code, String message, List<String> details, String traceId) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.details = details;
        this.traceId = traceId;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
