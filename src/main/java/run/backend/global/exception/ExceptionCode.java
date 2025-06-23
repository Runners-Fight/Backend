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
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, 2001, "잘못된 요청입니다.");

    // 3000:

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
