package run.backend.domain.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;
import run.backend.domain.record.entity.CrewRecord;
import run.backend.global.common.BaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Getter
@Table(name = "events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    private String place;

    @Column(name = "expected_participants")
    private Long expectedParticipants;

    @Column(name = "actual_participants")
    private Long actualParticipants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private CrewRecord record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_captain")
    private Member member;

    @Builder
    public Event(
            String title,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String place,
            Crew crew,
            CrewRecord record,
            Member member
    ) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.expectedParticipants = 0L;
        this.actualParticipants = 0L;
        this.crew = crew;
        this.record = record;
        this.member = member;
    }

    public void incrementExpectedParticipants() {
        this.expectedParticipants++;
    }

    public void incrementActualParticipants() {
        this.actualParticipants++;
    }
}
