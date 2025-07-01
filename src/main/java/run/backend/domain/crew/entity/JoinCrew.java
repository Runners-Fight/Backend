package run.backend.domain.crew.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.global.common.BaseEntity;

import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinCrew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_status")
    private JoinStatus joinStatus;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "crew_role")
//    privare Role role;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    void approveJoin() {
        this.joinStatus = JoinStatus.APPROVED;
        this.joinedDate = LocalDate.now();
    }

    @Builder
    public JoinCrew(
            JoinStatus joinStatus,
//            Role role,
            Member member,
            Crew crew
    ) {
        this.joinStatus = joinStatus;
//        this.role = role;
        this.member = member;
        this.crew = crew;
    }
}
