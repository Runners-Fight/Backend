package run.backend.domain.event.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum EventErrorCode implements ErrorCode {

    RUNNING_CAPTAIN_NOT_CREW_MEMBER(6001, "러닝캡이 크루원이 아닙니다."),
    EVENT_NOT_FOUND(6002, "일정을 찾을 수 없습니다."),
    ALREADY_JOINED_EVENT(6003, "이미 참여 요청이 되어있습니다.");

    private final int errorCode;
    private final String errorMessage;
}
