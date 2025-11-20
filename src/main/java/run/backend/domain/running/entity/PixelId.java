package run.backend.domain.running.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class PixelId implements Serializable, Comparable<PixelId> {

    private int x;
    private int y;

    public PixelId(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(PixelId other) {
        int xCompare = Integer.compare(this.x, other.x);
        if (xCompare != 0) {
            return xCompare;
        }
        return Integer.compare(this.y, other.y);
    }
}
