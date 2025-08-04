package run.backend.domain.crew.dto.request;

import run.backend.domain.member.enums.Role;

public record MemberRoleChangeRequest(
        Role role
) {
}
