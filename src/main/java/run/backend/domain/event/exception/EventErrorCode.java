package run.backend.domain.event.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum EventErrorCode implements ErrorCode {

    RUNNING_CAPTAIN_NOT_CREW_MEMBER(6001, "러닝캡이 크루원이 아닙니다.");

    private final int errorCode;
    private final String errorMessage;
}
