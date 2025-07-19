package run.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.event.entity.PeriodicEvent;

public interface PeriodicEventRepository extends JpaRepository<PeriodicEvent, Long> {
}
