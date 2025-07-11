package run.backend.domain.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_SIGNUP_TOKEN(3001, "유효하지 않은 가입 토큰입니다."),
    USER_ALREADY_EXISTS(3002, "이미 가입된 사용자입니다."),
    OAUTH_REQUEST_FAILED(3003, "외부 인증 서버와 통신 중 오류가 발생했습니다."),
    TOKEN_MISSING_AUTHORITY(3004, "토큰에 권한 정보가 없습니다."),
    INVALID_REFRESH_TOKEN(3005, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(3006, "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(3007, "리프레시 토큰이 만료되었습니다.");

    private final int errorCode;
    private final String errorMessage;
}
