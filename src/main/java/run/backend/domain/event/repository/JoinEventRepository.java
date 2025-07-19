package run.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.event.entity.JoinEvent;

public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {

}
