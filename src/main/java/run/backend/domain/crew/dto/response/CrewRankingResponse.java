package run.backend.domain.crew.dto.response;

public record CrewRankingResponse(
        Long crewId,
        String name,
        String image,
        int monthlyDistanceKm
) {
}
