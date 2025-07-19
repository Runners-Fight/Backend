package run.backend.domain.crew.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.common.CrewInviteCodeDto;
import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.dto.response.CrewProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.service.CrewServiceImpl;
import run.backend.domain.member.entity.Member;
import run.backend.global.annotation.member.Login;
import run.backend.global.annotation.member.MemberCrew;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
@Tag(name = "Crews", description = "Crew 관련 API")
public class CrewController {

    private final CrewServiceImpl crewService;

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
    @Operation(summary = "크루 정보 수정", description = "크루 정보 수정하는 API 입니다.")
    public CommonResponse<Void> updateCrewInfo(
            @Login Member member,
            @MemberCrew Crew crew,
            @RequestParam String imageStatus,
            @RequestPart(value = "data")CrewInfoRequest data,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        crewService.updateCrew(member, crew, imageStatus, image, data);
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
    @Operation(summary = "크루 가입", description = "크루 가입하는 API 입니다.")
    public CommonResponse<Void> joinCrew(
            @Login Member member,
            @PathVariable Long crewId) {

        crewService.joinCrew(member, crewId);
        return new CommonResponse<>("크루 가입 성공");
    }
}
