package run.backend.domain.running.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pixel {

    @EmbeddedId
    private PixelId id;

    private Long crewId;

    private LocalDateTime updatedAt;

    public Pixel(PixelId id, Long crewId, LocalDateTime updatedAt) {
        this.id = id;
        this.crewId = crewId;
        this.updatedAt = updatedAt;
    }

    public void updateCrew(Long crewId, LocalDateTime updatedAt) {
        this.crewId = crewId;
        this.updatedAt = updatedAt;
    }
}
