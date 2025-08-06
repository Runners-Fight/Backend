package run.backend.domain.event.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.member.entity.Member;

import java.util.Optional;

public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.member = :member")
    Optional<JoinEvent> findByEventAndMember(@Param("event") Event event, @Param("member") Member member);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event")
    List<JoinEvent> findByEvent(@Param("event") Event event);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.isRunning = true")
    List<JoinEvent> findActualParticipantsByEvent(@Param("event") Event event);

    @Query("SELECT j FROM JoinEvent j WHERE j.member = :member " +
           "AND j.event.date >= :startDate AND j.event.date <= :endDate")
    List<JoinEvent> findMonthlyParticipatedEvents(@Param("member") Member member, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);

    boolean existsByEventAndMember(Event event, Member member);
}
