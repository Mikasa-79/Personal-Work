package com.campushub.dto;

public class NotificationDtos {

    public static class NotificationResponse {
        private Long id;
        private String type;
        private String title;
        private String content;
        private Boolean readFlag;
        private Long relatedId;

        public NotificationResponse() {
        }

        public NotificationResponse(Long id, String type, String title, String content, Boolean readFlag, Long relatedId) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.content = content;
            this.readFlag = readFlag;
            this.relatedId = relatedId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Boolean getReadFlag() {
            return readFlag;
        }

        public void setReadFlag(Boolean readFlag) {
            this.readFlag = readFlag;
        }

        public Long getRelatedId() {
            return relatedId;
        }

        public void setRelatedId(Long relatedId) {
            this.relatedId = relatedId;
        }
    }
}
