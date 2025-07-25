package run.backend.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.enums.EventStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.record.entity.CrewRecord;
import run.backend.global.common.BaseEntity;


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

    private EventStatus status;

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
        this.expectedParticipants = 1L;
        this.actualParticipants = 0L;
        this.crew = crew;
        this.record = record;
        this.member = member;
        this.status = EventStatus.BEFORE;
    }

    public void incrementExpectedParticipants() {
        this.expectedParticipants++;
    }

    public void incrementActualParticipants() {
        this.actualParticipants++;
    }

    public void updateEvent(
        String title,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String place,
        Member runningCaptain
    ) {
        if (title != null) {
            this.title = title;
        }
        if (date != null) {
            this.date = date;
        }
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (endTime != null) {
            this.endTime = endTime;
        }
        if (place != null) {
            this.place = place;
        }
        if (runningCaptain != null) {
            this.member = runningCaptain;
        }
    }
    
    public String getDistanceKm() {
        if (record != null && record.getDistance() != null) {
            return record.getDistance().toString();
        }
        return "0";
    }
    
    public String getRunningTime() {
        if (record != null && record.getDurationTime() != null) {
            long totalSeconds = record.getDurationTime();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return "00:00:00";
    }
}
