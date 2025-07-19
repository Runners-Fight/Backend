package run.backend.domain.crew.service;

import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.common.CrewInviteCodeDto;
import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.dto.response.*;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;

import java.time.YearMonth;

public interface CrewService {

    CrewInviteCodeDto createCrew(Member member, String imageStatus, MultipartFile image, CrewInfoRequest crewInfoRequest);

    void updateCrew(Member member, Crew crew, String imageStatus, MultipartFile image, CrewInfoRequest crewInfoRequest);

    CrewInviteCodeDto getCrewInviteCode(Crew crew);

    CrewProfileResponse getCrewByInviteCode(String inviteCode);

    void joinCrew(Member member, Long crewId);

//    CrewSearchResponse searchCrew(String crewName);
//
//    CrewInfoResponse getCrewInfo(Long crewId);
//
//    CrewMonthlyCanlendarResponse getCrewMonthlyCalendar(Long crewId, YearMonth yearMonth);
//
//    CrewUpcomingEventResponse getUpcomingEvents(Long crewId);
//
//    CrewMemberResponse getCrewMemberProfile(Long crewId);
//
//    void updateCrewMemberRole(Long memberId, Role role);
//
//    CrewSearchResponse getRankCrew();
}
