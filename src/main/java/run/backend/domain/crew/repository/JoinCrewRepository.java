package run.backend.domain.crew.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.dto.query.CrewMemberProfileDto;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;

import java.util.List;
import java.util.Optional;
import run.backend.domain.event.dto.response.EventCreationValidationDto;

public interface JoinCrewRepository extends JpaRepository<JoinCrew, Long> {

    boolean existsByMemberAndJoinStatus(Member member, JoinStatus joinStatus);

    Optional<JoinCrew> findByMember(Member member);

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

    @Query("""
        SELECT captainJoin.member
        FROM JoinCrew captainJoin
        WHERE captainJoin.member.id = :runningCaptainId
        AND captainJoin.crew.id = :crewId
        AND captainJoin.joinStatus = :status
        """)
    Optional<Member> findCrewMemberById(
        @Param("runningCaptainId") Long runningCaptainId,
        @Param("crewId") Long crewId,
        @Param("status") JoinStatus status
    );

    @Query("""
        SELECT new run.backend.domain.crew.dto.query.CrewMemberProfileDto(
            m.profileImage,
            m.nickname,
            m.role
        )
        FROM JoinCrew jc
            JOIN jc.member m
        WHERE jc.crew.id = :crewId
        AND jc.joinStatus = :status
    """)
    List<CrewMemberProfileDto> findAllCrewMemberByCrewId(@Param("crewId") Long crewId, @Param("status") JoinStatus status);
}
