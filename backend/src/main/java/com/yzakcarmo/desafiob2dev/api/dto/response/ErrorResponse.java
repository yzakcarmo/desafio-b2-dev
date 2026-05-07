package com.yzakcarmo.desafiob2dev.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;
    private final List<String> details;
    private final String traceId;
    private final OffsetDateTime timestamp;

    public ErrorResponse(int status, String code, String message,
                         List<String> details, String traceId) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.details = details;
        this.traceId = traceId;
        this.timestamp = OffsetDateTime.now();
    }

    public int getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public String getTraceId() { return traceId; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}