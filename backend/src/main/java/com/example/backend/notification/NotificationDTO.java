package com.example.backend.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String candidateName;
    private String candidatEmail;
    private Long applicationId;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
