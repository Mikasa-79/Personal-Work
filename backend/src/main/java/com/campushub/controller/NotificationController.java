package com.campushub.controller;

import com.campushub.dto.NotificationDtos;
import com.campushub.entity.User;
import com.campushub.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDtos.NotificationResponse>> listNotifications(
            ) {
        User user = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(notificationService.listNotifications(user));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationDtos.NotificationResponse> markRead(
            @PathVariable Long id) {
        User user = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(notificationService.markRead(id, user));
    }

}
