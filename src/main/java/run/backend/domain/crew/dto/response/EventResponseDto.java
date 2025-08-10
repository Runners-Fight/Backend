package run.backend.domain.crew.dto.response;

import java.util.List;

public record EventResponseDto(
        List<EventProfileResponse> eventProfiles
) {
}
