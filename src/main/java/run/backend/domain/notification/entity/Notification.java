package run.backend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.member.entity.Member;
import run.backend.domain.notification.enums.MessageType;
import run.backend.global.common.BaseEntity;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;

    @Column(name = "target_id")
    private Long targetId;       // crew 가입 요청이라면 targetId에 joinCrew id 저장

    @Column(name = "is_read")
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Builder
    public Notification(
            String message,
            MessageType messageType,
            Long targetId,
            Member sender,
            Member receiver
    ) {
       this.message = message;
       this.messageType = messageType;
       this.targetId = targetId;
       this.isRead = false;
       this.sender = sender;
       this.receiver = receiver;
    }
}
