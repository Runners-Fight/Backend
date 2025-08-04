package run.backend.domain.crew.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.common.CrewInviteCodeDto;
import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.dto.request.MemberRoleChangeRequest;
import run.backend.domain.crew.dto.response.*;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.service.CrewEventService;
import run.backend.domain.crew.service.CrewMemberService;
import run.backend.domain.crew.service.CrewRankingService;
import run.backend.domain.crew.service.CrewService;
import run.backend.domain.member.entity.Member;
import run.backend.global.annotation.member.Login;
import run.backend.global.annotation.member.MemberCrew;
import run.backend.global.common.response.CommonResponse;
import run.backend.global.common.response.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
@Tag(name = "Crews", description = "Crew 관련 API")
public class CrewController {

    private final CrewService crewService;
    private final CrewEventService crewEventService;
    private final CrewMemberService crewMemberService;
    private final CrewRankingService crewRankingService;

    @PostMapping
    @Operation(summary = "크루 생성", description = "크루 생성하는 API 입니다.")
    public CommonResponse<CrewInviteCodeDto> createCrew(
            @Login Member member,
            @RequestParam String imageStatus,
            @RequestPart(value = "data")CrewInfoRequest data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        CrewInviteCodeDto response = crewService.createCrew(member, imageStatus, image, data);
        return new CommonResponse<>("크루 생성 성공", response);
    }

    @PatchMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('LEADER')")
    @Operation(summary = "크루 정보 수정", description = "크루 정보 수정하는 API 입니다.")
    public CommonResponse<Void> updateCrewInfo(
            @MemberCrew Crew crew,
            @RequestParam String imageStatus,
            @RequestPart(value = "data")CrewInfoRequest data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        crewService.updateCrew(crew, imageStatus, image, data);
        return new CommonResponse<>("크루 정보 수정 성공");
    }

    @GetMapping("/{crewId}/invite-code")
    @Operation(summary = "크루의 초대 코드 조회", description = "크루의 초대 코드 조회하는 API 입니다.")
    public CommonResponse<CrewInviteCodeDto> getCrewInviteCode(@MemberCrew Crew crew) {

        CrewInviteCodeDto response = crewService.getCrewInviteCode(crew);
        return new CommonResponse<>("크루 초대 코드 조회 성공", response);
    }

    @GetMapping("/invite-code/{inviteCode}")
    @Operation(summary = "초대 코드로 크루 조회", description = "초대 코드로 크루를 조회하는 API 입니다.")
    public CommonResponse<CrewProfileResponse> getCrewByInviteCode(@PathVariable String inviteCode) {

        CrewProfileResponse response = crewService.getCrewByInviteCode(inviteCode);
        return new CommonResponse<>("크루 조회 성공", response);
    }

    @PostMapping("/{crewId}/join")
    @Operation(summary = "크루 가입 요청", description = "크루 가입 요청하는 API 입니다.")
    public CommonResponse<Void> joinCrew(
            @Login Member member,
            @PathVariable Long crewId) {

        crewService.joinCrew(member, crewId);
        return new CommonResponse<>("크루 가입 요청 성공");
    }

    @GetMapping
    @Operation(summary = "크루 기본 정보 조회", description = "크루 기본 정보를 조회하는 API 입니다.")
    public CommonResponse<CrewBaseInfoResponse> getCrewBaseInfo(@MemberCrew Crew crew) {

        CrewBaseInfoResponse response = crewService.getCrewBaseInfo(crew);
        return new CommonResponse<>("크루 기본 정보 조회 성공", response);
    }

    @GetMapping("/events/weekly")
    @Operation(summary = "weekly 기록 조회", description = "크루의 weekly 기록 조회하는 API 입니다.")
    public CommonResponse<CrewWeeklyEventResponse> getWeeklyRecord(@MemberCrew Crew crew) {

        CrewWeeklyEventResponse response = crewEventService.getCrewWeeklyEvent(crew);
        return new CommonResponse<>("크루 주간 기록 조회 성공", response);
    }

    @GetMapping("/events/monthly")
    @Operation(summary = "monthly 일정 조회", description = "크루의 monthly 일정 조회하는 API 입니다.")
    public CommonResponse<CrewMonthlyCanlendarResponse> getMonthlyEvent(
            @MemberCrew Crew crew,
            @RequestParam int year,
            @RequestParam int month) {

        CrewMonthlyCanlendarResponse response = crewEventService.getCrewMonthlyCalendar(crew, year, month);
        return new CommonResponse<>("크루 월간 기록 조회 성공", response);
    }

    @GetMapping("/events/upcoming")
    @Operation(summary = "upcoming 일정 조회", description = "크루의 upcoming 일정 조회하는 API 입니다.")
    public CommonResponse<CrewUpcomingEventResponse> getUpcomingEvent(@MemberCrew Crew crew) {

        CrewUpcomingEventResponse response = crewEventService.getCrewUpcomingEvent(crew);
        return new CommonResponse<>("크루 다가오는 일정 조회 성공", response);
    }

    @GetMapping("/rankings")
    public CommonResponse<PageResponse<CrewRankingResponse>> getCrewRankings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        PageResponse<CrewRankingResponse> response = crewRankingService.getCrewRanking(page, size);
        return new CommonResponse<>("크루 랭킹 조회 성공", response);
    }

    @GetMapping("/rankings/status")
    public CommonResponse<CrewRankingStatusResponse> getCrewRankingsStatus(@MemberCrew Crew crew) {

        CrewRankingStatusResponse response = crewRankingService.getCrewRankingStatus(crew);
        return new CommonResponse<>("크루 땅따먹기 현황 조회 성공", response);
    }

    @GetMapping("/members")
    @Operation(summary = "크루원 조회", description = "전체 크루원 조회하는 API 입니다.")
    public CommonResponse<CrewMemberResponse> getCrewMembers(@MemberCrew Crew crew) {

        CrewMemberResponse response = crewMemberService.getCrewMembers(crew);
        return new CommonResponse<>("크루원 조회 성공", response);
    }

    @PostMapping("/members/{memberId}/role")
    @PreAuthorize("hasRole('MANAGER') or hasRole('LEADER')")
    @Operation(summary = "크루원 역할 변경", description = "크루원 역할 변경하는 API 입니다.")
    public CommonResponse<Void> updateCrewMemberRole(
            @PathVariable Long memberId,
            @RequestBody MemberRoleChangeRequest request
    ) {
        crewMemberService.updateCrewMemberRole(memberId, request);
        return new CommonResponse<>("크루원 역할 변경 성공");
    }

    @GetMapping("/search")
    @Operation(summary = "크루 검색", description = "크루 이름으로 검색 API 입니다.")
    public CommonResponse<PageResponse<CrewSearchResponse>> searchCrew(
            @RequestParam String crewName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<CrewSearchResponse> response = crewService.searchCrewsByName(crewName, page, size);
        return new CommonResponse<>("크루 검색 성공", response);
    }
}
