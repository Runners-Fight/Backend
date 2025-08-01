package run.backend.domain.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.member.entity.Member;
import run.backend.global.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "join_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_running")
    private boolean isRunning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Builder
    public JoinEvent(
            Member member,
            Event event
    ) {
        this.isRunning = false;
        this.member = member;
        this.event = event;
    }

    public void softDelete() {
        this.setDeletedAt(LocalDateTime.now());
    }
}
