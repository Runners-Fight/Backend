package run.backend.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauthId(String oauthId);

    @Query("""
    SELECT jc.crew
    FROM JoinCrew jc
    WHERE jc.member.id = :memberId
        AND jc.joinStatus = :status
    """)
    Optional<Crew> findCrewByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") JoinStatus status);
}
