package com.campushub.controller;

import com.campushub.dto.OrderDtos;
import com.campushub.entity.User;
import com.campushub.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<java.util.List<OrderDtos.OrderResponse>> listOrders() {
        User user = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(orderService.listOrders(user));
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<OrderDtos.OrderResponse> acceptRequest(
            @PathVariable Long requestId) {
        User provider = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(orderService.acceptRequest(requestId, provider));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderDtos.OrderResponse> confirmOrder(
            @PathVariable Long orderId) {
        User requester = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(orderService.confirmOrder(orderId, requester));
    }

    @PostMapping("/{orderId}/start")
    public ResponseEntity<OrderDtos.OrderResponse> startOrder(
            @PathVariable Long orderId) {
        User provider = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(orderService.startOrder(orderId, provider));
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<OrderDtos.OrderResponse> completeOrder(
            @PathVariable Long orderId) {
        User operator = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(orderService.completeOrder(orderId, operator));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDtos.OrderResponse> getOrder(
            @PathVariable Long orderId) {
        User user = com.campushub.security.SecurityContextUtils.getCurrentUser();
        return ResponseEntity.ok(orderService.getOrder(orderId, user));
    }

}
