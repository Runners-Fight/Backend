package run.backend.domain.crew.entity;

import jakarta.persistence.*;
import lombok.*;
import run.backend.global.common.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "crews")
@AllArgsConstructor
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

    @Column(name = "captured_distance_total")
    private BigDecimal capturedDistanceTotal;

    @Column(name = "monthly_time_total")
    private Long monthlyTimeTotal;

    @Column(name = "monthly_score_total")
    private BigDecimal monthlyScoreTotal;   // monthlyDistanceTotal(70%) + capturedDistanceTotal(30%)

    public void incrementMemberCount() {
        this.memberCount++;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateImage(String newImageName) {
        this.image = newImageName;
    }

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
        this.capturedDistanceTotal = BigDecimal.ZERO;
        this.monthlyTimeTotal = 0L;
        this.monthlyScoreTotal = BigDecimal.ZERO;
    }
}
