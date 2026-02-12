package com.lovepaws.app.api.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ApiErrorDTO {
    LocalDateTime timestamp;
    int status;
    String error;
    String message;
}
