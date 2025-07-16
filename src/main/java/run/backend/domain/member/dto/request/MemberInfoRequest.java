package run.backend.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;
import run.backend.domain.member.enums.Gender;

public record MemberInfoRequest(
        @Nullable
        @Schema(description = "성별", example = "FEMALE / MALE", nullable = true)
        Gender gender,
        @Nullable
        @Schema(description = "나이", example = "24", nullable = true)
        Integer age,
        @Nullable
        @Schema(description = "닉네임", example = "러너스", nullable = true)
        String nickname
) {
}
