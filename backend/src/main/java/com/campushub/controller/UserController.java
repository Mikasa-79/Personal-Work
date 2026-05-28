package com.campushub.controller;

import com.campushub.dto.UserDtos;
import com.campushub.entity.User;
import com.campushub.security.SecurityContextUtils;
import com.campushub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDtos.ProfileResponse> currentUser() {
        User user = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(userService.currentUserProfile(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDtos.ProfileResponse> updateProfile(
            @Valid @RequestBody UserDtos.UpdateRequest request) {
        User user = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }
}
