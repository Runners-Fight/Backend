package run.backend.domain.crew.dto.response;

import java.util.List;

public record CrewMemberResponse(
        List<CrewMemberProfileResponse> managers,
        List<CrewMemberProfileResponse> members
) {
}
