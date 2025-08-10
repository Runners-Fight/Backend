package run.backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.response.EventResponseDto;
import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberCrewStatusResponse;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.dto.response.MemberParticipatedCountResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.service.MemberService;
import run.backend.global.annotation.member.Login;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Members", description = "Member 관련 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "유저 정보 조회", description = "마이페이지 상단 유저 정보를 조회하는 API 입니다.")
    public CommonResponse<MemberInfoResponse> getMemberInfo(@Login Member member) {

        MemberInfoResponse response = memberService.getMemberInfo(member);
        return new CommonResponse<>("유저 정보 조회 성공", response);
    }

    @PostMapping
    @Operation(summary = "유저 정보 수정", description = "마이페이지에서 유저 정보를 수정하는 API 입니다.")
    public CommonResponse<Void> updateMemberInfo(
            @Login Member member,
            @RequestParam String imageStatus,
            @RequestPart(value = "data") MemberInfoRequest data,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        memberService.updateMemberInfo(member, imageStatus, image, data);
        return new CommonResponse<>("유저 정보 수정 성공");
    }

    @GetMapping("/me/crews/exists")
    @Operation(summary = "가입된 크루가 있는지 확인", description = "유저가 가입된 크루가 있는지 확인하는 API 입니다.")
    public CommonResponse<MemberCrewStatusResponse> getMembersCrewExists(@Login Member member) {

        MemberCrewStatusResponse response = memberService.getMembersCrewExists(member);
        return new CommonResponse<>("유저 크루 가입 여부 조회 완료", response);
    }

    @GetMapping("/participated/preview")
    @Operation(summary = "유저의 이번 시즌 참여 횟수 조회", description = "유저가 이번 달에 참여한 러닝 횟수를 조회하는 API 입니다.")
    public CommonResponse<MemberParticipatedCountResponse> getParticipatedCount(@Login Member member) {

        MemberParticipatedCountResponse response = memberService.getParticipatedEventCount(member);
        return new CommonResponse<>("유저의 이번 시즌 참여 횟수 조회 성공", response);
    }

    @GetMapping("/participated")
    @Operation(summary = "유저의 이번 시즌 참여한 러닝 리스트 조회", description = "유저가 이번 시즌에 참여한 러닝 리스트를 조회하는 API 입니다.")
    public CommonResponse<EventResponseDto> getParticipated(@Login Member member) {

        EventResponseDto response = memberService.getParticipatedEvent(member);
        return new CommonResponse<>("러닝에 대한 상세 참여 내역 조회 성공", response);
    }
}
