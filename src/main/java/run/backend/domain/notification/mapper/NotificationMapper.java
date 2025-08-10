package run.backend.domain.notification.mapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import run.backend.domain.notification.dto.NotificationItem;
import run.backend.domain.notification.entity.Notification;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    @Mapping(target = "notificationId", source = "id")
    @Mapping(target = "type", source = "messageType.description")
    @Mapping(target = "timeAgo", expression = "java(calculateTimeAgo(notification.getCreatedAt()))")
    NotificationItem toNotificationItem(Notification notification);

    List<NotificationItem> toNotificationItemList(List<Notification> notifications);

    default String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);

        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }
}
