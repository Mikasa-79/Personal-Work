package com.campushub.service;

import com.campushub.dto.OrderDtos;
import com.campushub.entity.HelpRequest;
import com.campushub.entity.Order;
import com.campushub.entity.User;
import com.campushub.repository.HelpRequestRepository;
import com.campushub.repository.OrderRepository;
import com.campushub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        HelpRequestRepository helpRequestRepository,
                        UserRepository userRepository,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.helpRequestRepository = helpRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderDtos.OrderResponse acceptRequest(Long requestId, User provider) {
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("请求不存在"));
        if (!"OPEN".equals(request.getStatus()) || !"APPROVED".equals(request.getAuditStatus())) {
            throw new IllegalArgumentException("该请求无法接单");
        }
        if (request.getPublisher().getId().equals(provider.getId())) {
            throw new IllegalArgumentException("不能接自己的请求");
        }

        Order order = new Order();
        order.setRequest(request);
        order.setRequester(request.getPublisher());
        order.setProvider(provider);
        order.setStatus("ACCEPTED");
        order.setAcceptedAt(LocalDateTime.now());

        request.setStatus("LOCKED");
        helpRequestRepository.save(request);
        Order saved = orderRepository.save(order);

        notificationService.sendNotification(provider, "ORDER_ACCEPTED", "接单成功", "你已成功接单。", "ORDER", saved.getId());
        notificationService.sendNotification(request.getPublisher(), "ORDER_ACCEPTED", "有人接单", "你的任务已被接单，等待确认。", "ORDER", saved.getId());

        return mapResponse(saved);
    }

    @Transactional
    public OrderDtos.OrderResponse confirmOrder(Long orderId, User requester) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!order.getRequester().getId().equals(requester.getId())) {
            throw new IllegalArgumentException("只有请求者可以确认订单");
        }
        if (!"ACCEPTED".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前无法确认");
        }
        order.setStatus("CONFIRMED");
        order.setConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);

        notificationService.sendNotification(order.getProvider(), "ORDER_CONFIRMED", "订单已确认", "请求者已确认你的服务。", "ORDER", order.getId());
        return mapResponse(order);
    }

    @Transactional
    public OrderDtos.OrderResponse startOrder(Long orderId, User provider) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!order.getProvider().getId().equals(provider.getId())) {
            throw new IllegalArgumentException("只有服务方可以开始订单");
        }
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前无法开始");
        }
        order.setStatus("IN_PROGRESS");
        order.setStartedAt(LocalDateTime.now());
        orderRepository.save(order);

        notificationService.sendNotification(order.getRequester(), "ORDER_STARTED", "服务已开始", "服务方已开始执行任务。", "ORDER", order.getId());
        return mapResponse(order);
    }

    @Transactional
    public OrderDtos.OrderResponse completeOrder(Long orderId, User operator) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!order.getRequester().getId().equals(operator.getId()) && !order.getProvider().getId().equals(operator.getId())) {
            throw new IllegalArgumentException("只有订单双方可完成订单");
        }
        if (!"IN_PROGRESS".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前无法完成");
        }
        order.setStatus("COMPLETED");
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);

        notificationService.sendNotification(order.getRequester(), "ORDER_COMPLETED", "订单已完成", "任务已被标记为完成。", "ORDER", order.getId());
        notificationService.sendNotification(order.getProvider(), "ORDER_COMPLETED", "订单已完成", "任务已被标记为完成。", "ORDER", order.getId());
        return mapResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderDtos.OrderResponse getOrder(Long orderId, User viewer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!order.getRequester().getId().equals(viewer.getId()) && !order.getProvider().getId().equals(viewer.getId())) {
            throw new IllegalArgumentException("无权查看该订单");
        }
        return mapResponse(order);
    }

    @Transactional(readOnly = true)
    public java.util.List<OrderDtos.OrderResponse> listOrders(User user) {
        return orderRepository.findByRequesterIdOrProviderId(user.getId(), user.getId())
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    private OrderDtos.OrderResponse mapResponse(Order order) {
        return new OrderDtos.OrderResponse(
                order.getId(),
                order.getRequest().getId(),
                order.getRequester().getId(),
                order.getProvider().getId(),
                order.getStatus()
        );
    }
}
