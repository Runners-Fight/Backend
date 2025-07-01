package run.backend.domain.record.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.global.common.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal distance;

    @Column(name = "step_count")
    private Long stepCount;

    @Column(name = "duration_time")
    private Long durationTime;

    @Column(name = "average_pace")
    private Long averagePace;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Builder
    public CrewRecord(
            BigDecimal distance,
            Long stepCount,
            Long durationTime,
            Long averagePace,
            LocalTime startTime,
            LocalTime endTime
    ) {
        this.distance = distance;
        this.stepCount = stepCount;
        this.durationTime = durationTime;
        this.averagePace = averagePace;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
