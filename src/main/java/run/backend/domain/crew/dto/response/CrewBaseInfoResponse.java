package run.backend.domain.crew.dto.response;

import lombok.Builder;
import run.backend.domain.crew.entity.Crew;

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

    public static CrewBaseInfoResponse of(int rank, Crew crew) {

        return CrewBaseInfoResponse.builder()
                .rank(rank)
                .image(crew.getImage())
                .name(crew.getName())
                .description(crew.getDescription())
                .memberCount(crew.getMemberCount())
                .monthlyDistanceTotal(crew.getMonthlyDistanceTotal())
                .monthlyTimeTotal(crew.getMonthlyTimeTotal())
                .build();
    }
}
