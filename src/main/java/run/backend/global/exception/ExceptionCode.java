package run.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // 1000: Success Code

    // 2000: Common Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2000, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요."),
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, 2001, "잘못된 요청입니다."),


    // 3000: Auth Error
    INVALID_SIGNUP_TOKEN(HttpStatus.UNAUTHORIZED, 4001, "유효하지 않은 가입 토큰입니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, 4002, "이미 가입된 사용자입니다."),
    OAUTH_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 4003, "외부 인증 서버와 통신 중 오류가 발생했습니다."),
    TOKEN_MISSING_AUTHORITY(HttpStatus.UNAUTHORIZED, 4004, "토큰에 권한 정보가 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
