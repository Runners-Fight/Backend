package run.backend.domain.crew.dto.response;

import java.util.List;

public record CrewMonthlyCanlendarResponse(
        List<CrewRecordResponse> records,
        List<CrewEventResponse> events
) {
}
