package run.backend.domain.crew.dto.response;

import java.util.List;

public record CrewMemberResponse(
        List<CrewMemberProfile> crewMembers
) {
}
