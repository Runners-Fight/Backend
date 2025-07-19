package run.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
