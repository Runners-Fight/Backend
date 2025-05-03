package run.backend.domain.crew.dto.response;

import java.util.List;

public record CrewSearchResponse(
        List<CrewProfileResponse> crewProfiles
) {
}
