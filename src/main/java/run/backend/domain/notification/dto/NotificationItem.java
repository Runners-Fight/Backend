package run.backend.domain.notification.dto;

public record NotificationItem(String notificationId, String type, String message, String timeAgo) {

}