package run.backend.domain.crew.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;
import run.backend.global.common.BaseEntity;

import java.time.LocalDate;


@Entity
@Getter
@Table(name = "join_crews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE join_crews SET deleted_at = NOW() WHERE id = ?")
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

    public void approveJoin() {
        this.role = Role.MEMBER;
        this.joinedDate = LocalDate.now();
        this.joinStatus = JoinStatus.APPROVED;
    }

    public static JoinCrew createLeaderJoin(Member member, Crew crew) {
        return JoinCrew.builder()
                .crew(crew)
                .member(member)
                .role(Role.LEADER)
                .joinStatus(JoinStatus.APPROVED)
                .joinedDate(LocalDate.now())
                .build();
    }

    public static JoinCrew createAppliedJoin(Member member, Crew crew) {
        return JoinCrew.builder()
                .crew(crew)
                .member(member)
                .role(Role.MEMBER)
                .joinStatus(JoinStatus.APPLIED)
                .build();
    }

    @Builder
    private JoinCrew(
            Member member,
            Crew crew,
            Role role,
            JoinStatus joinStatus,
            LocalDate joinedDate

    ) {
        this.crew = crew;
        this.member = member;
        this.role = role;
        this.joinStatus = joinStatus;
        this.joinedDate = joinedDate;
    }
}
