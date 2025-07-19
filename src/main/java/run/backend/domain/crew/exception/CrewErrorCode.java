package run.backend.domain.crew.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum CrewErrorCode implements ErrorCode {

    ALREADY_JOINED_CREW(7001, "이미 가입한 크루가 있습니다."),
    NOT_FOUND_CREW(7002, "해당 크루를 찾을 수 없습니다.");


    private final int errorCode;
    private final String errorMessage;
}
