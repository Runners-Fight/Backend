package run.backend.domain.crew.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.event.dto.response.EventCreationValidationDto;

public interface JoinCrewRepository extends JpaRepository<JoinCrew, Long> {

    boolean existsByMemberAndJoinStatus(Member member, JoinStatus joinStatus);

    @Query("""
    SELECT jc.member
    FROM JoinCrew jc
        JOIN Crew c ON jc.crew = c
    WHERE jc.role = :role
    """)
    Member findCrewLeader(@Param("role") Role role, Crew crew);

    @Query("SELECT jc FROM JoinCrew jc WHERE jc.member.id = :memberId AND jc.joinStatus = :status")
    Optional<JoinCrew> findByMemberIdAndJoinStatus(@Param("memberId") Long memberId,
                                                   @Param("status") JoinStatus status);

    @Query("""
        SELECT new run.backend.domain.event.dto.response.EventCreationValidationDto(
            requesterJoin.crew,
            captainJoin.member
        )
        FROM JoinCrew requesterJoin 
        INNER JOIN JoinCrew captainJoin ON requesterJoin.crew.id = captainJoin.crew.id
        WHERE requesterJoin.member.id = :requesterId 
        AND requesterJoin.joinStatus = :status
        AND captainJoin.member.id = :runningCaptainId
        AND captainJoin.joinStatus = :status
        """)
    Optional<EventCreationValidationDto> validateEventCreation(
        @Param("requesterId") Long requesterId, 
        @Param("runningCaptainId") Long runningCaptainId,
        @Param("status") JoinStatus status
    );
}
