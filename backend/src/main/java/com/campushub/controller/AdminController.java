package com.campushub.controller;

import com.campushub.dto.AdminDtos;
import com.campushub.dto.HelpRequestDtos;
import com.campushub.entity.User;
import com.campushub.security.SecurityContextUtils;
import com.campushub.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<AdminDtos.RequestAuditResponse>> listPendingRequests() {
        return ResponseEntity.ok(adminService.listPendingRequests());
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<HelpRequestDtos.Response> approveRequest(@PathVariable Long requestId) {
        User admin = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(adminService.approveRequest(requestId, admin));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<HelpRequestDtos.Response> rejectRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody AdminDtos.RejectRequest request) {
        User admin = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(adminService.rejectRequest(requestId, request.getReason(), admin));
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminDtos.UserAdminResponse>> listUsers() {
        return ResponseEntity.ok(adminService.listUsers());
    }

    @PostMapping("/users/{userId}/disable")
    public ResponseEntity<AdminDtos.UserAdminResponse> disableUser(@PathVariable Long userId) {
        User admin = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(adminService.disableUser(userId, admin));
    }

    @PostMapping("/users/{userId}/enable")
    public ResponseEntity<AdminDtos.UserAdminResponse> enableUser(@PathVariable Long userId) {
        User admin = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(adminService.enableUser(userId, admin));
    }
}
