package run.backend.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberCrewStatusResponse(

        @Schema(description = "유저의 크루 가입 상태 (NONE: 가입된 크루 없음, APPLIED: 가입 대기 중, APPROVED: 가입 완료")
        String status
) {
}
