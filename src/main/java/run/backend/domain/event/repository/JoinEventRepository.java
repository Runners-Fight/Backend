package run.backend.domain.event.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.dto.response.EventProfileResponse;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.enums.EventStatus;
import run.backend.domain.member.entity.Member;

public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.member = :member")
    Optional<JoinEvent> findByEventAndMember(@Param("event") Event event, @Param("member") Member member);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event")
    List<JoinEvent> findByEvent(@Param("event") Event event);
    
    @Query("SELECT j FROM JoinEvent j WHERE j.event = :event AND j.event.status = :status")
    List<JoinEvent> findActualParticipantsByEvent(@Param("event") Event event, @Param("status") EventStatus status);

    @Query("SELECT j FROM JoinEvent j WHERE j.member = :member " +
           "AND j.event.date >= :startDate AND j.event.date <= :endDate " +
           "AND j.event.status = :status")
    List<JoinEvent> findMonthlyParticipatedEvents(@Param("member") Member member, 
                                                  @Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("status") EventStatus status);

    @Query("""
        SELECT new run.backend.domain.crew.dto.response.EventProfileResponse(\
        e.id, e.title, e.date, e.startTime, e.endTime, e.expectedParticipants) \
        FROM JoinEvent j JOIN j.event e \
        WHERE j.member = :member \
        AND e.date >= :startDate AND e.date <= :endDate \
        AND e.status = :status ORDER BY e.date DESC""")
    List<EventProfileResponse> findMonthlyCompletedEvents(@Param("member") Member member,
                                                             @Param("startDate") LocalDate startDate, 
                                                             @Param("endDate") LocalDate endDate,
                                                             @Param("status") EventStatus status);

    boolean existsByEventAndMember(Event event, Member member);
}
