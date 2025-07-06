package com.chatapp.chatbackend.exception;

import lombok.*;
import java.time.LocalDateTime;

// This is a DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
