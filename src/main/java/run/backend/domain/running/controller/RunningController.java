package run.backend.domain.running.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.running.dto.request.Coordinate;
import run.backend.domain.running.service.RunningService;
import run.backend.global.annotation.member.MemberCrew;
import run.backend.global.common.response.CommonResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/running")
@Tag(name = "러닝 API", description = "러닝 관련 API")
public class RunningController {

    private final RunningService runningService;

    @PostMapping("/route")
    @Operation(summary = "러닝 좌표 저장", description = "크루의 러닝 좌표를 저장하는 API 입니다.")
    public ResponseEntity<CommonResponse<Void>> uploadRoute(
            @RequestBody List<Coordinate> coordinates,
            @MemberCrew Crew crew) {

        runningService.processRunningRoute(crew.getId(), coordinates);
        return ResponseEntity.ok(new CommonResponse<>("크루 러닝 좌표 저장 완료"));
    }
}
