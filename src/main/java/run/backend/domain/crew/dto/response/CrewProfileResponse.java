package run.backend.domain.crew.dto.response;

import lombok.Builder;

@Builder
public record CrewProfileResponse(
        String crewImage,
        String crewName,
        String crewDescription,
        Long memberCount,
        String leaderImage,
        String leaderName
) {
}
