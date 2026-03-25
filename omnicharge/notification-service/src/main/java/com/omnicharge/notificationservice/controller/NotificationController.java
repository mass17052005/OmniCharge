package com.omnicharge.notificationservice.controller;

import com.omnicharge.notificationservice.entity.Notification;
import com.omnicharge.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications by user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                notificationService.getNotificationsByUserId(userId));
    }

    @Operation(summary = "Get notifications by recharge ID")
    @GetMapping("/recharge/{rechargeId}")
    public ResponseEntity<List<Notification>> getNotificationsByRechargeId(
            @PathVariable Long rechargeId) {
        return ResponseEntity.ok(
                notificationService.getNotificationsByRechargeId(rechargeId));
    }

    @Operation(summary = "Get notification by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                notificationService.getNotificationById(id));
    }
}
