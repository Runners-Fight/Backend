package run.backend.domain.crew.dto.response;

public record CrewRankingStatusResponse(
        int ranking,
        int totalDistanceKm,
        int capturedDistanceKm
) {
}
