package run.backend.domain.crew.dto.response;

import run.backend.domain.crew.dto.common.DayStatusDto;
import java.util.Map;

public record CrewMonthlyCanlendarResponse(

        Map<Integer, DayStatusDto> monthlyRunningStatus
) {
}
