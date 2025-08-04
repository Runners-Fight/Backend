package run.backend.domain.crew.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import run.backend.domain.crew.dto.query.CrewProfileDto;
import run.backend.domain.crew.entity.Crew;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    Optional<Crew> findByInviteCodeAndDeletedAtIsNull(String inviteCode);

    Page<Crew> findAllByDeletedAtIsNullOrderByMonthlyScoreTotalDesc(Pageable pageable);


    @Query("""
    SELECT c.id
    FROM Crew c
    WHERE c.deletedAt IS NULL
    ORDER BY c.monthlyScoreTotal DESC
    """)
    List<Long> findAllActiveCrewIdsOrderByScoreDesc();

    @Query("""
    SELECT new run.backend.domain.crew.dto.query.CrewProfileDto(
        c.image,
        c.name,
        c.description
    )
    FROM Crew c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
    AND c.deletedAt IS NULL
    """)
    Page<CrewProfileDto> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
