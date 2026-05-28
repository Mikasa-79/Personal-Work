package com.campushub.controller;

import com.campushub.dto.OrderDtos;
import com.campushub.dto.OrderDtos.ReviewRequest;
import com.campushub.entity.User;
import com.campushub.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{orderId}/reviews")
    public ResponseEntity<OrderDtos.OrderResponse> submitReview(
            @PathVariable Long orderId,
            @Valid @RequestBody ReviewRequest request) {
        User reviewer = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(reviewService.submitReview(orderId, reviewer, request.getRating(), request.getComment()));
    }
}
