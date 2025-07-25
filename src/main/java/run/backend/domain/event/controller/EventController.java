package run.backend.domain.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.dto.response.EventDetailResponse;
import run.backend.domain.event.service.EventService;
import run.backend.domain.member.entity.Member;
import run.backend.global.annotation.member.Login;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "일정 관련 API")
public class EventController {

    private final EventService eventService;

    @PostMapping
//    @PreAuthorize("hasRole('MANAGER') or hasRole('LEADER')")
    @Operation(summary = "일정 생성", description = "러닝 일정을 생성합니다. LEADER 또는 MANAGER 권한이 필요합니다.")
    public CommonResponse<Void> createEvent(
        @RequestBody EventInfoRequest eventInfoRequest,
        @Login Member member
    ) {

        eventService.createEvent(eventInfoRequest, member);
        return new CommonResponse<>("러닝 일정 생성 성공");
    }

    @PatchMapping("/{eventId}")
//    @PreAuthorize("hasRole('MANAGER') or hasRole('LEADER')")
    @Operation(summary = "일정 수정", description = "러닝 일정을 수정합니다. LEADER 또는 MANAGER 권한이 필요합니다.")
    public CommonResponse<Void> updateEvent(
        @PathVariable Long eventId,
        @RequestBody EventInfoRequest eventUpdateRequest
    ) {

        eventService.updateEvent(eventId, eventUpdateRequest);
        return new CommonResponse<>("러닝 일정 수정 성공");
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "일정 상세 조회", description = "러닝 일정을 조회합니다.")
    public CommonResponse<EventDetailResponse> getEventDetail(@PathVariable Long eventId) {
        EventDetailResponse response = eventService.getEventDetail(eventId);
        return new CommonResponse<>("러닝 일정 상세 조회 성공", response);
    }

    @PostMapping("/{eventId}/join-requests")
    @Operation(summary = "러닝 참여", description = "러닝 일정에 참여 요청합니다")
    public CommonResponse<Void> joinEvent(@PathVariable Long eventId, @Login Member member) {
        eventService.joinEvent(eventId, member);
        return new CommonResponse<>("러닝 참여 요청 완료");
    }
}
