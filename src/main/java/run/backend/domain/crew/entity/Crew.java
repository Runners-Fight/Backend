package run.backend.domain.crew.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.global.common.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String image;

    @Column(name = "invite_code")
    private String inviteCode;

    @Column(name = "member_count")
    private Long memberCount;

    @Column(name = "monthly_distance_total")
    private BigDecimal monthlyDistanceTotal;

    @Column(name = "monthly_time_total")
    private Long monthlyTimeTotal;

    @Column(name = "monthly_score_total")
    private BigDecimal monthlyScoreTotal;

    @Builder
    public Crew (
            String name,
            String description,
            String image
    ) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.inviteCode = UUID.randomUUID().toString();
        this.memberCount = 1L;
        this.monthlyDistanceTotal = BigDecimal.ZERO;
        this.monthlyTimeTotal = 0L;
        this.monthlyScoreTotal = BigDecimal.ZERO;
    }
}
