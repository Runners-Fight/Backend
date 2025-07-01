package run.backend.domain.crew.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;
import run.backend.global.common.BaseEntity;

import java.time.LocalDate;


@Entity
@Getter
@Table(name = "join_crews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinCrew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_status")
    private JoinStatus joinStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "crew_role")
    private Role role;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    void approveJoin() {
        this.role = Role.MEMBER;
        this.joinedDate = LocalDate.now();
        this.joinStatus = JoinStatus.APPROVED;
    }

    @Builder
    public JoinCrew(
            Member member,
            Crew crew
    ) {
        this.crew = crew;
        this.member = member;
        this.joinStatus = JoinStatus.APPLIED;
    }
}
