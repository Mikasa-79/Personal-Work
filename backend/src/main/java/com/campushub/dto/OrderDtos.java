package com.campushub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderDtos {

    public static class OrderResponse {
        private Long id;
        private Long requestId;
        private Long requesterId;
        private Long providerId;
        private String status;

        public OrderResponse() {
        }

        public OrderResponse(Long id, Long requestId, Long requesterId, Long providerId, String status) {
            this.id = id;
            this.requestId = requestId;
            this.requesterId = requesterId;
            this.providerId = providerId;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public Long getRequesterId() {
            return requesterId;
        }

        public void setRequesterId(Long requesterId) {
            this.requesterId = requesterId;
        }

        public Long getProviderId() {
            return providerId;
        }

        public void setProviderId(Long providerId) {
            this.providerId = providerId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class ReviewRequest {
        @NotNull
        private Integer rating;

        @NotBlank
        private String comment;

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
