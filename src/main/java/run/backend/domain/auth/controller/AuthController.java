package run.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.auth.dto.request.CodeRequest;
import run.backend.domain.auth.dto.request.SignupRequest;
import run.backend.domain.auth.dto.response.SignupResponse;
import run.backend.domain.auth.dto.response.TokenResponse;
import run.backend.domain.auth.service.AuthService;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 요청 API 입니다.")
    @PostMapping("/{provider}")
    public ResponseEntity<CommonResponse<SignupResponse>> socialLogin(
        @PathVariable String provider, @RequestBody CodeRequest codeRequest) {

        SignupResponse response = authService.socialLogin(provider, codeRequest.code());

        return ResponseEntity.ok(new CommonResponse<>("소셜 로그인 요청에 성공했습니다.", response));
    }

    @Operation(summary = "온보딩 유저 정보 입력", description = "온보딩 유저 정보를 입력하는 API 입니다.")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<TokenResponse>> signup(
        @RequestPart("signupRequest") SignupRequest signupRequest,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        TokenResponse response = authService.completeSignup(signupRequest, profileImage);

        return ResponseEntity.ok(new CommonResponse<>("회원가입이 완료되었습니다.", response));
    }

    @Operation(summary = "토큰 재발급", description = "액세스 토큰 재발급 API 입니다.")
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<TokenResponse>> refresh(
        @RequestHeader("Authorization") String authorizationHeader) {

        TokenResponse response = authService.refreshTokens(authorizationHeader);

        return ResponseEntity.ok(new CommonResponse<>("토큰이 갱신되었습니다.", response));
    }
}
