package run.backend.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.member.entity.Member;
import run.backend.domain.notification.entity.Notification;
import run.backend.domain.notification.enums.MessageType;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.receiver = :member AND n.isRead = true ORDER BY n.createdAt DESC")
    List<Notification> findReadNotificationsByMember(@Param("member") Member member);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :member AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByMember(@Param("member") Member member);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :member AND n.messageType = :messageType AND n.isRead = true ORDER BY n.createdAt DESC")
    List<Notification> findReadNotificationsByMemberAndType(@Param("member") Member member, @Param("messageType") MessageType messageType);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :member AND n.messageType = :messageType AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByMemberAndType(@Param("member") Member member, @Param("messageType") MessageType messageType);
}
