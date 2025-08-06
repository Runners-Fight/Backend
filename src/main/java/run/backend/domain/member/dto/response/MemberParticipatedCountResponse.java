package run.backend.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberParticipatedCountResponse(
        @Schema(description = "유저의 이번 시즌 참여 횟수", example = "4")
        String participatedCount
) {
}
