package run.backend.domain.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.service.EventServiceImpl;
import run.backend.domain.member.entity.Member;
import run.backend.global.annotation.member.Login;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "일정 관련 API")
public class EventController {
    
    private final EventServiceImpl eventService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('LEADER')")
    @Operation(summary = "일정 생성", description = "러닝 일정를 생성합니다. LEADER 또는 MANAGER 권한이 필요합니다.")
    public CommonResponse<Void> createEvent(
            @RequestBody EventInfoRequest eventInfoRequest,
            @Login Member member
    ) {
        
        eventService.createEvent(eventInfoRequest, member);
        return new CommonResponse<>("일정 생성 성공");
    }
}
