package com.campushub.service;

import com.campushub.entity.Notification;
import com.campushub.entity.User;
import com.campushub.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendNotification(User receiver, String type, String title, String content, String relatedType, Long relatedId) {
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setReadFlag(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<com.campushub.dto.NotificationDtos.NotificationResponse> listNotifications(User receiver) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiver.getId())
                .stream()
                .map(notification -> new com.campushub.dto.NotificationDtos.NotificationResponse(
                        notification.getId(),
                        notification.getType(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getReadFlag(),
                        notification.getRelatedId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public com.campushub.dto.NotificationDtos.NotificationResponse markRead(Long id, User receiver) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("通知不存在"));
        if (!notification.getReceiver().getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("无权标记该通知");
        }
        notification.setReadFlag(true);
        notification.setReadAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(notification);
        return new com.campushub.dto.NotificationDtos.NotificationResponse(
                saved.getId(),
                saved.getType(),
                saved.getTitle(),
                saved.getContent(),
                saved.getReadFlag(),
                saved.getRelatedId()
        );
    }
}
