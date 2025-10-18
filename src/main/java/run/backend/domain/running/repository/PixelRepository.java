package run.backend.domain.running.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.running.entity.Pixel;
import run.backend.domain.running.entity.PixelId;

public interface PixelRepository extends JpaRepository<Pixel, PixelId> {
}
