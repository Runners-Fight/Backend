package run.backend.domain.running.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum RunningErrorCode implements ErrorCode {

    INVALID_COORDINATE(8001, "유효하지 않은 좌표 범위입니다.");

    private final int errorCode;
    private final String errorMessage;
}
