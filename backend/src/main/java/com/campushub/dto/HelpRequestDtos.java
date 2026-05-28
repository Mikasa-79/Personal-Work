package com.campushub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class HelpRequestDtos {

    public static class CreateRequest {
        @NotBlank
        @Size(min = 5, max = 80)
        private String title;

        @NotBlank
        @Size(min = 10, max = 2000)
        private String description;

        @NotBlank
        @Size(min = 2, max = 100)
        private String location;

        @NotNull
        @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime expectedTime;

        private String category;
        private Double reward;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public LocalDateTime getExpectedTime() {
            return expectedTime;
        }

        public void setExpectedTime(LocalDateTime expectedTime) {
            this.expectedTime = expectedTime;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Double getReward() {
            return reward;
        }

        public void setReward(Double reward) {
            this.reward = reward;
        }
    }

    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String location;
        private LocalDateTime expectedTime;
        private Double reward;
        private String category;
        private String status;
        private Long publisherId;
        private String publisherNickname;

        public Response() {
        }

        public Response(Long id, String title, String description, String location, LocalDateTime expectedTime, Double reward, String category, String status, Long publisherId, String publisherNickname) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.location = location;
            this.expectedTime = expectedTime;
            this.reward = reward;
            this.category = category;
            this.status = status;
            this.publisherId = publisherId;
            this.publisherNickname = publisherNickname;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public LocalDateTime getExpectedTime() {
            return expectedTime;
        }

        public void setExpectedTime(LocalDateTime expectedTime) {
            this.expectedTime = expectedTime;
        }

        public Double getReward() {
            return reward;
        }

        public void setReward(Double reward) {
            this.reward = reward;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getPublisherId() {
            return publisherId;
        }

        public void setPublisherId(Long publisherId) {
            this.publisherId = publisherId;
        }

        public String getPublisherNickname() {
            return publisherNickname;
        }

        public void setPublisherNickname(String publisherNickname) {
            this.publisherNickname = publisherNickname;
        }
    }
}
