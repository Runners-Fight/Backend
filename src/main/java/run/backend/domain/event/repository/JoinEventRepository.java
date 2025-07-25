package run.backend.domain.event.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.member.entity.Member;

import java.util.Optional;

public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
    
    void deleteByEventAndMember(Event event, Member member);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.member = :member AND j.deletedAt IS NULL")
    Optional<JoinEvent> findByEventAndMember(@Param("event") Event event, @Param("member") Member member);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.deletedAt IS NULL")
    List<JoinEvent> findByEventAndNotDeleted(@Param("event") Event event);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.isRunning = true AND j.deletedAt IS NULL")
    List<JoinEvent> findActualParticipantsByEvent(@Param("event") Event event);

}
