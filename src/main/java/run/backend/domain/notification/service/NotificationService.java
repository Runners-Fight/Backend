package run.backend.domain.notification.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.member.entity.Member;
import run.backend.domain.notification.dto.NotificationItem;
import run.backend.domain.notification.dto.NotificationResponse;
import run.backend.domain.notification.entity.Notification;
import run.backend.domain.notification.enums.MessageType;
import run.backend.domain.notification.exception.NotificationException;
import run.backend.domain.notification.mapper.NotificationMapper;
import run.backend.domain.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationResponse getNotifications(Member member, String type) {
        MessageType messageType = convertStringToMessageType(type);

        List<Notification> readNotifications;
        List<Notification> unreadNotifications;

        if (messageType == MessageType.ALL) {
            readNotifications = notificationRepository.findReadNotificationsByMember(member);
            unreadNotifications = notificationRepository.findUnreadNotificationsByMember(member);
        } else {
            readNotifications = notificationRepository.findReadNotificationsByMemberAndType(member, messageType);
            unreadNotifications = notificationRepository.findUnreadNotificationsByMemberAndType(member, messageType);
        }

        List<NotificationItem> readItems = notificationMapper.toNotificationItemList(readNotifications);
        List<NotificationItem> unreadItems = notificationMapper.toNotificationItemList(unreadNotifications);

        return new NotificationResponse(readItems, unreadItems);
    }

    private MessageType convertStringToMessageType(String type) {
        return switch (type.toLowerCase()) {
            case "all" -> MessageType.ALL;
            case "crew" -> MessageType.CREW;
            case "battle" -> MessageType.BATTLE;
            default -> throw new NotificationException.InvalidNotificationType();
        };
    }
}
