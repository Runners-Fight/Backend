package run.backend.domain.crew.dto.query;

import run.backend.domain.member.enums.Role;

public record CrewMemberProfileDto(
        String image,
        String nickname,
        Role role
) {
}
