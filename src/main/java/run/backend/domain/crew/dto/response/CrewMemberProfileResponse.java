package run.backend.domain.crew.dto.response;

import run.backend.domain.member.enums.Role;

public record CrewMemberProfileResponse(
        String image,
        String nickname,
        Role role
) {
}
