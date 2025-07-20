package run.backend.domain.crew.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CrewBaseInfoResponse(

        int rank,
        String image,
        String name,
        String description,
        Long memberCount,
        BigDecimal monthlyDistanceTotal,
        Long monthlyTimeTotal

) {
}
