package run.backend.domain.event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.enums.WeekDay;
import run.backend.domain.member.entity.Member;
import run.backend.global.common.BaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "periodic_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodicEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "base_date")
    private LocalDate baseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_cycle")
    private RepeatCycle repeatCycle;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_days")
    private WeekDay repeatDays;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "running_captain")
    private Member member;

    @Builder
    public PeriodicEvent(
            String title,
            LocalDate baseDate,
            RepeatCycle repeatCycle,
            WeekDay repeatDays,
            LocalTime startTime,
            LocalTime endTime,
            String place,
            Crew crew,
            Member member
    ) {
        this.title = title;
        this.baseDate = baseDate;
        this.repeatCycle = repeatCycle;
        this.repeatDays = repeatDays;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.crew = crew;
        this.member = member;
    }
}
