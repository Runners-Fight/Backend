package run.backend.domain.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;

public interface JoinCrewRepository extends JpaRepository<JoinCrew, Long> {

    boolean existsByMemberAndJoinStatus(Member member, JoinStatus joinStatus);

    @Query("""
    SELECT jc.member
    FROM JoinCrew jc
        JOIN Crew c ON jc.crew = c
    WHERE jc.role = :role
    """)
    Member findCrewLeader(@Param("role") Role role, Crew crew);
}
