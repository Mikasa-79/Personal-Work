package com.campushub.dto;

import jakarta.validation.constraints.Size;

public class UserDtos {

    public static class ProfileResponse {
        private Long id;
        private String studentNo;
        private String nickname;
        private String college;
        private String contact;
        private String role;
        private Boolean admin;
        private Integer creditScore;
        private String status;

        public ProfileResponse() {
        }

        public ProfileResponse(Long id, String studentNo, String nickname, String college, String contact, String role, Boolean admin, Integer creditScore, String status) {
            this.id = id;
            this.studentNo = studentNo;
            this.nickname = nickname;
            this.college = college;
            this.contact = contact;
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

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
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

    public static class UpdateRequest {
        @Size(min = 2, max = 30)
        private String nickname;

        @Size(max = 80)
        private String college;

        @Size(max = 120)
        private String contact;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }
    }
}
