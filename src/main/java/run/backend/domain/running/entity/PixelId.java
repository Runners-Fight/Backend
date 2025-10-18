package run.backend.domain.running.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PixelId implements Serializable {

    private int x;
    private int y;

    public PixelId(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
