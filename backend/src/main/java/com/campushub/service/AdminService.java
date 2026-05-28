package com.campushub.service;

import com.campushub.dto.AdminDtos;
import com.campushub.dto.HelpRequestDtos;
import com.campushub.entity.AuditLog;
import com.campushub.entity.HelpRequest;
import com.campushub.entity.User;
import com.campushub.repository.AuditLogRepository;
import com.campushub.repository.HelpRequestRepository;
import com.campushub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminService(HelpRequestRepository helpRequestRepository,
                        UserRepository userRepository,
                        AuditLogRepository auditLogRepository) {
        this.helpRequestRepository = helpRequestRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminDtos.RequestAuditResponse> listPendingRequests() {
        return helpRequestRepository.findByStatusAndAuditStatus("OPEN", "PENDING")
                .stream()
                .map(this::toAuditResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminDtos.UserAdminResponse> listUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new AdminDtos.UserAdminResponse(
                        user.getId(),
                        user.getStudentNo(),
                        user.getNickname(),
                        user.getRole(),
                        user.getAdmin(),
                        user.getCreditScore(),
                        user.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public HelpRequestDtos.Response approveRequest(Long requestId, User admin) {
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("请求不存在"));
        if (!"OPEN".equals(request.getStatus()) || !"PENDING".equals(request.getAuditStatus())) {
            throw new IllegalArgumentException("请求当前不可审批");
        }
        request.setAuditStatus("APPROVED");
        HelpRequest saved = helpRequestRepository.save(request);
        recordAudit(admin, "HELP_REQUEST", requestId, "APPROVE", null);
        return toHelpRequestResponse(saved);
    }

    @Transactional
    public HelpRequestDtos.Response rejectRequest(Long requestId, String reason, User admin) {
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("请求不存在"));
        if (!"OPEN".equals(request.getStatus()) || !"PENDING".equals(request.getAuditStatus())) {
            throw new IllegalArgumentException("请求当前不可驳回");
        }
        request.setAuditStatus("REJECTED");
        HelpRequest saved = helpRequestRepository.save(request);
        recordAudit(admin, "HELP_REQUEST", requestId, "REJECT", reason);
        return toHelpRequestResponse(saved);
    }

    @Transactional
    public AdminDtos.UserAdminResponse disableUser(Long userId, User admin) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        target.setStatus("DISABLED");
        User saved = userRepository.save(target);
        recordAudit(admin, "USER", userId, "DISABLE", null);
        return toUserAdminResponse(saved);
    }

    @Transactional
    public AdminDtos.UserAdminResponse enableUser(Long userId, User admin) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        target.setStatus("ACTIVE");
        User saved = userRepository.save(target);
        recordAudit(admin, "USER", userId, "ENABLE", null);
        return toUserAdminResponse(saved);
    }

    private AdminDtos.RequestAuditResponse toAuditResponse(HelpRequest request) {
        return new AdminDtos.RequestAuditResponse(
                request.getId(),
                request.getTitle(),
                request.getStatus(),
                request.getAuditStatus(),
                request.getPublisher().getId(),
                request.getPublisher().getNickname()
        );
    }

    private AdminDtos.UserAdminResponse toUserAdminResponse(User user) {
        return new AdminDtos.UserAdminResponse(
                user.getId(),
                user.getStudentNo(),
                user.getNickname(),
                user.getRole(),
                user.getAdmin(),
                user.getCreditScore(),
                user.getStatus()
        );
    }

    private HelpRequestDtos.Response toHelpRequestResponse(HelpRequest request) {
        return new HelpRequestDtos.Response(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getExpectedTime(),
                request.getReward(),
                request.getCategory(),
                request.getStatus(),
                request.getPublisher().getId(),
                request.getPublisher().getNickname()
        );
    }

    private void recordAudit(User admin, String targetType, Long targetId, String action, String reason) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdmin(admin);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLogRepository.save(auditLog);
    }
}
