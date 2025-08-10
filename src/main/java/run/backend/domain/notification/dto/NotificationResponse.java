package run.backend.domain.notification.dto;

import java.util.List;

public record NotificationResponse(List<NotificationItem> read, List<NotificationItem> unread) {

}
