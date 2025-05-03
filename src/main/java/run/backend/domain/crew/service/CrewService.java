package run.backend.domain.crew.service;

import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.dto.response.*;
import run.backend.domain.crew.enumerate.CrewMemberRole;

import java.time.YearMonth;

public interface CrewService {

    void updateCrew(CrewInfoRequest crewInfoRequest);

    CrewSearchResponse searchCrew(String crewName);

    CrewInfoResponse getCrewInfo(Long crewId);

    CrewMonthlyCanlendarResponse getCrewMonthlyCalendar(Long crewId, YearMonth yearMonth);

    CrewUpcomingEventResponse getUpcomingEvents(Long crewId);

    CrewMemberResponse getCrewMemberProfile(Long crewId);

    void updateCrewMemberRole(Long memberId, CrewMemberRole crewMemberRole);

    CrewSearchResponse getRankCrew();
}
