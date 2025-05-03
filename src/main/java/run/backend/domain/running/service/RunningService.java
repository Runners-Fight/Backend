package run.backend.domain.running.service;

import run.backend.domain.running.dto.response.CrewRunningSummaryResponse;
import run.backend.domain.running.dto.response.RunningRecordResponse;

public interface RunningService {

    void startRunning(Long eventId);

    RunningRecordResponse stopRunning(Long eventId);

    void joinRunning(Long memberId, Long eventId);

    CrewRunningSummaryResponse getCrewRunningSummary(Long crewId);
}
