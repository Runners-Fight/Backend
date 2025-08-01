package run.backend.domain.crew.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.crew.entity.Crew;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    Optional<Crew> findByInviteCode(String inviteCode);


    Page<Crew> findAllByOrderByMonthlyScoreTotalDesc(Pageable pageable);
}
