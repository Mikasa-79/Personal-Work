package com.campushub.service;

import com.campushub.dto.OrderDtos;
import com.campushub.entity.Order;
import com.campushub.entity.Review;
import com.campushub.entity.User;
import com.campushub.repository.OrderRepository;
import com.campushub.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository,
                         OrderRepository orderRepository,
                         NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public OrderDtos.OrderResponse submitReview(Long orderId, User reviewer, Integer rating, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        if (!"COMPLETED".equals(order.getStatus()) && !"REVIEWED".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前无法评价");
        }

        User reviewee;
        if (order.getRequester().getId().equals(reviewer.getId())) {
            reviewee = order.getProvider();
        } else if (order.getProvider().getId().equals(reviewer.getId())) {
            reviewee = order.getRequester();
        } else {
            throw new IllegalArgumentException("无权评价该订单");
        }

        reviewRepository.findByOrderIdAndReviewerId(orderId, reviewer.getId())
                .ifPresent(r -> { throw new IllegalArgumentException("不能重复评价"); });

        Review review = new Review();
        review.setOrder(order);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreditDelta(calculateCreditDelta(rating));
        reviewRepository.save(review);

        reviewee.setCreditScore(reviewee.getCreditScore() + review.getCreditDelta());
        order.setReviewedAt(java.time.LocalDateTime.now());
        order.setStatus("REVIEWED");
        orderRepository.save(order);

        notificationService.sendNotification(reviewee, "REVIEW_CREATED", "你收到新评价", "你已收到新的评价。", "ORDER", order.getId());
        return new OrderDtos.OrderResponse(order.getId(), order.getRequest().getId(), order.getRequester().getId(), order.getProvider().getId(), order.getStatus());
    }

    private int calculateCreditDelta(Integer rating) {
        if (rating == null) {
            return 0;
        }
        return Math.max(-10, Math.min(10, rating - 3) * 2);
    }
}
