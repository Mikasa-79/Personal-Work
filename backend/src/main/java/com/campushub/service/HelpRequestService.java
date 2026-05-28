package com.campushub.service;

import com.campushub.dto.HelpRequestDtos;
import com.campushub.entity.HelpRequest;
import com.campushub.entity.User;
import com.campushub.repository.HelpRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelpRequestService {

    private final HelpRequestRepository helpRequestRepository;

    public HelpRequestService(HelpRequestRepository helpRequestRepository) {
        this.helpRequestRepository = helpRequestRepository;
    }

    @Transactional
    public HelpRequestDtos.Response createRequest(User publisher, HelpRequestDtos.CreateRequest request) {
        HelpRequest entity = new HelpRequest();
        entity.setPublisher(publisher);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setLocation(request.getLocation());
        entity.setExpectedTime(request.getExpectedTime());
        entity.setReward(request.getReward() != null ? request.getReward() : 0.0);
        entity.setCategory(request.getCategory() != null ? request.getCategory() : "OTHER");
        entity.setStatus("OPEN");
        entity.setAuditStatus("PENDING");

        HelpRequest saved = helpRequestRepository.save(entity);
        return mapResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<HelpRequestDtos.Response> listVisibleRequests(User user) {
        List<HelpRequest> approvedRequests = helpRequestRepository.findByStatusAndAuditStatus("OPEN", "APPROVED");
        List<HelpRequest> ownOpenRequests = helpRequestRepository.findByPublisherIdAndStatus(user.getId(), "OPEN");

        List<HelpRequest> merged = approvedRequests.stream()
                .collect(Collectors.toMap(HelpRequest::getId, request -> request))
                .values().stream()
                .collect(Collectors.toList());

        ownOpenRequests.stream()
                .filter(request -> merged.stream().noneMatch(existing -> existing.getId().equals(request.getId())))
                .forEach(merged::add);

        return merged.stream()
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HelpRequestDtos.Response> listPendingRequests() {
        return helpRequestRepository.findByStatusAndAuditStatus("OPEN", "PENDING")
                .stream()
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HelpRequestDtos.Response approveRequest(Long requestId) {
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("请求不存在"));
        if (!"OPEN".equals(request.getStatus()) || !"PENDING".equals(request.getAuditStatus())) {
            throw new IllegalArgumentException("请求当前不可审批");
        }
        request.setAuditStatus("APPROVED");
        HelpRequest saved = helpRequestRepository.save(request);
        return mapResponse(saved);
    }

    @Transactional
    public HelpRequestDtos.Response rejectRequest(Long requestId, String reason) {
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("请求不存在"));
        if (!"OPEN".equals(request.getStatus()) || !"PENDING".equals(request.getAuditStatus())) {
            throw new IllegalArgumentException("请求当前不可驳回");
        }
        request.setAuditStatus("REJECTED");
        HelpRequest saved = helpRequestRepository.save(request);
        return mapResponse(saved);
    }

    @Transactional(readOnly = true)
    public HelpRequestDtos.Response getRequestById(Long id) {
        return helpRequestRepository.findById(id)
                .map(this::mapResponse)
                .orElseThrow(() -> new IllegalArgumentException("请求不存在"));
    }

    private HelpRequestDtos.Response mapResponse(HelpRequest entity) {
        return new HelpRequestDtos.Response(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getExpectedTime(),
                entity.getReward(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getPublisher().getId(),
                entity.getPublisher().getStudentNo()
        );
    }
}
