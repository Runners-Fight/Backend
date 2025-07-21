package run.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.entity.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByCrewAndDateBetween(Crew crew, LocalDate startOfWeek, LocalDate endOfWeek);

    List<Event> findAllByCrewAndDateAfter(Crew crew, LocalDate today);

}
