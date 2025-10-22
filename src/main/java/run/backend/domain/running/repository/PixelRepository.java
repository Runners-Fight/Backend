package run.backend.domain.running.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import run.backend.domain.running.entity.Pixel;
import run.backend.domain.running.entity.PixelId;

import java.util.Optional;

public interface PixelRepository extends JpaRepository<Pixel, PixelId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pixel p WHERE p.id = :id")
    Optional<Pixel> findByIdWithLock(@Param("id") PixelId id);
}
