package run.backend.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.service.MemberServiceImpl;
import run.backend.global.annotation.member.Login;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Members", description = "Member 관련 API")
public class MemberController {

    private final MemberServiceImpl memberService;

    @Operation(summary = "유저 정보 조회", description = "마이페이지 상단 유저 정보를 조회하는 API 입니다.")
    @GetMapping
    public CommonResponse<MemberInfoResponse> getMemberInfo(@Login Member member) {

        MemberInfoResponse response = memberService.getMemberInfo(member);
        return new CommonResponse<>("유저 정보 조회 성공", response);
    }

    @Operation(summary = "유저 정보 수정", description = "마이페이지에서 유저 정보를 수정하는 API 입니다.")
    @PostMapping
    public CommonResponse<Void> updateMemberInfo(
            @Login Member member,
            @RequestParam String imageStatus,
            @RequestPart(value = "data") MemberInfoRequest data,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        memberService.updateMemberInfo(member, imageStatus, image, data);
        return new CommonResponse<>("유저 정보 수정 성공");
    }
}
