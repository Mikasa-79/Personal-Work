package com.campushub.service;

import com.campushub.dto.AuthDtos;
import com.campushub.entity.User;
import com.campushub.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.Optional;
import com.campushub.security.JwtUtil;
import com.campushub.service.RefreshTokenService;
import com.campushub.entity.RefreshToken;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByStudentNo(request.getStudentNo())) {
            throw new IllegalArgumentException("学号已注册");
        }

        User user = new User();
        user.setStudentNo(request.getStudentNo());
        user.setPasswordHash(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setRole("REQUESTER");
        user.setStatus("ACTIVE");
        user.setAdmin(false);
        user.setNickname(request.getNickname());
        user.setCollege(request.getCollege());
        user.setContact(request.getContact());
        user.setCreditScore(100);
        user.setAuthToken(UUID.randomUUID().toString());

        User saved = userRepository.save(user);
        String jwt = jwtUtil.generateToken(saved.getId(), saved.getRole());
        RefreshToken refresh = refreshTokenService.createRefreshToken(saved);
        AuthDtos.AuthResponse resp = buildResponse(saved);
        resp.setToken(jwt);
        resp.setRefreshToken(refresh.getToken());
        saved.setAuthToken(jwt);
        userRepository.save(saved);
        return resp;
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByStudentNo(request.getStudentNo())
                .orElseThrow(() -> new IllegalArgumentException("学号或密码错误"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("学号或密码错误");
        }

        String jwt = jwtUtil.generateToken(user.getId(), user.getRole());
        RefreshToken refresh = refreshTokenService.createRefreshToken(user);
        user.setAuthToken(jwt);
        userRepository.save(user);
        AuthDtos.AuthResponse resp = buildResponse(user);
        resp.setToken(jwt);
        resp.setRefreshToken(refresh.getToken());
        return resp;
    }

    public Optional<User> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByAuthToken(token);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public String generateJwtForUser(User user) {
        String jwt = jwtUtil.generateToken(user.getId(), user.getRole());
        user.setAuthToken(jwt);
        userRepository.save(user);
        return jwt;
    }

    @Transactional
    public void logout(User user) {
        if (user == null || user.getId() == null) return;
        user.setAuthToken(null);
        userRepository.save(user);
        // remove any refresh tokens
        refreshTokenService.deleteByUserId(user.getId());
    }

    private AuthDtos.AuthResponse buildResponse(User user) {
        return new AuthDtos.AuthResponse(
                user.getAuthToken(),
                user.getId(),
                user.getStudentNo(),
                user.getNickname(),
                user.getRole(),
                user.getAdmin(),
                user.getCreditScore()
        );
    }
}
