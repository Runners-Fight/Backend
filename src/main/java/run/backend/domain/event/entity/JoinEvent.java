package run.backend.domain.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.member.entity.Member;

@Entity
@Getter
@Table(name = "join_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinEvent {

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
        this.event.incrementExpectedParticipants();
    }
}
