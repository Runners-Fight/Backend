package run.backend.domain.member.dto.request;

import run.backend.domain.member.enums.Gender;

public record MemberInfoRequest(
        Gender gender,
        int age,
        String nickname
) {
}
