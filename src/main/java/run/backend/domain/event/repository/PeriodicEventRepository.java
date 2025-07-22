package run.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.entity.PeriodicEvent;

import java.time.LocalTime;
import java.util.Optional;

public interface PeriodicEventRepository extends JpaRepository<PeriodicEvent, Long> {
    
    @Query("""
        SELECT pe 
        FROM PeriodicEvent pe 
        WHERE pe.crew = :crew 
        AND pe.title = :title 
        AND pe.startTime = :startTime 
        AND pe.endTime = :endTime
        """)
    Optional<PeriodicEvent> findByCrewAndTitleAndTime(
        @Param("crew") Crew crew,
        @Param("title") String title,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
}
