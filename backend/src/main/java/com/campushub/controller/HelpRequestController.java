package com.campushub.controller;

import com.campushub.dto.HelpRequestDtos;
import com.campushub.entity.User;
import com.campushub.security.SecurityContextUtils;
import com.campushub.service.HelpRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class HelpRequestController {

    private final HelpRequestService requestService;

    public HelpRequestController(HelpRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<HelpRequestDtos.Response>> listRequests() {
        User user = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(requestService.listVisibleRequests(user));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<HelpRequestDtos.Response> getRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId));
    }

    @PostMapping
    public ResponseEntity<HelpRequestDtos.Response> createRequest(
            @Valid @RequestBody HelpRequestDtos.CreateRequest request) {
        User publisher = SecurityContextUtils.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(publisher, request));
    }
}
