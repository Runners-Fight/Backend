package run.backend.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.backend.domain.auth.dto.request.CodeRequest;
import run.backend.domain.auth.dto.request.SignupRequest;
import run.backend.domain.auth.dto.response.SignupResponse;
import run.backend.domain.auth.service.AuthService;
import run.backend.domain.auth.dto.response.TokenResponse;
import run.backend.global.common.response.CommonResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/{provider}")
    public ResponseEntity<CommonResponse<SignupResponse>> socialLogin(
        @PathVariable String provider, @RequestBody CodeRequest codeRequest) {

        SignupResponse response = authService.socialLogin(provider, codeRequest.code());

        return ResponseEntity.ok(new CommonResponse<>("소셜 로그인 요청에 성공했습니다.", response));
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<TokenResponse>> signup(
        @RequestBody SignupRequest signupRequest) {

        TokenResponse response = authService.completeSignup(signupRequest);

        return ResponseEntity.ok(new CommonResponse<>("회원가입이 완료되었습니다.", response));
    }
}
