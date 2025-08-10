package run.backend.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.backend.domain.member.entity.Member;
import run.backend.domain.notification.dto.NotificationResponse;
import run.backend.domain.notification.service.NotificationService;
import run.backend.global.annotation.member.Login;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(name = "알림 API", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(
            summary = "알림 조회",
            description = "사용자의 알림 목록을 조회합니다. type 파라미터로 전체, 크루, 대결 알림을 필터링할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public ResponseEntity<CommonResponse<NotificationResponse>> getNotifications(
            @Parameter(description = "알림 타입 (all, crew, battle)", example = "all")
            @RequestParam(value = "type", defaultValue = "all") String type,
            @Login Member member
    ) {
        NotificationResponse response = notificationService.getNotifications(member, type);
        return ResponseEntity.ok(new CommonResponse<>("알림 조회 성공", response));
    }
}
