package com.campushub.controller;

import com.campushub.dto.AuthDtos;
import com.campushub.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.campushub.service.RefreshTokenService;
import com.campushub.entity.RefreshToken;
import com.campushub.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        User current = com.campushub.security.SecurityContextUtils.getCurrentUser();
        authService.logout(current);
        return ResponseEntity.noContent().build();
    }

    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDtos.AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken rt = refreshTokenService.verify(request.getRefreshToken());
        User user = rt.getUser();
        // rotate: delete old tokens and create a new one
        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken newRt = refreshTokenService.createRefreshToken(user);
        String jwt = authService.generateJwtForUser(user);
        AuthDtos.AuthResponse resp = authService.findById(user.getId())
                .map(u -> {
                    AuthDtos.AuthResponse r = new AuthDtos.AuthResponse();
                    r.setToken(jwt);
                    r.setRefreshToken(newRt.getToken());
                    r.setUserId(u.getId());
                    r.setStudentNo(u.getStudentNo());
                    r.setNickname(u.getNickname());
                    r.setRole(u.getRole());
                    r.setAdmin(u.getAdmin());
                    r.setCreditScore(u.getCreditScore());
                    return r;
                }).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return ResponseEntity.ok(resp);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
