package com.campushub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminDtos {

    public static class RequestAuditResponse {
        private Long id;
        private String title;
        private String status;
        private String auditStatus;
        private Long publisherId;
        private String publisherNickname;

        public RequestAuditResponse() {
        }

        public RequestAuditResponse(Long id, String title, String status, String auditStatus, Long publisherId, String publisherNickname) {
            this.id = id;
            this.title = title;
            this.status = status;
            this.auditStatus = auditStatus;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAuditStatus() {
            return auditStatus;
        }

        public void setAuditStatus(String auditStatus) {
            this.auditStatus = auditStatus;
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

    public static class RejectRequest {
        @NotBlank
        @Size(min = 3, max = 200)
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class UserAdminResponse {
        private Long id;
        private String studentNo;
        private String nickname;
        private String role;
        private Boolean admin;
        private Integer creditScore;
        private String status;

        public UserAdminResponse() {
        }

        public UserAdminResponse(Long id, String studentNo, String nickname, String role, Boolean admin, Integer creditScore, String status) {
            this.id = id;
            this.studentNo = studentNo;
            this.nickname = nickname;
            this.role = role;
            this.admin = admin;
            this.creditScore = creditScore;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentNo() {
            return studentNo;
        }

        public void setStudentNo(String studentNo) {
            this.studentNo = studentNo;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Boolean getAdmin() {
            return admin;
        }

        public void setAdmin(Boolean admin) {
            this.admin = admin;
        }

        public Integer getCreditScore() {
            return creditScore;
        }

        public void setCreditScore(Integer creditScore) {
            this.creditScore = creditScore;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
