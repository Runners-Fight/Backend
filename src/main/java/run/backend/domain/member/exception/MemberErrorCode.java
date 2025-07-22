package run.backend.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_JOINED_CREW(5001, "가입한 크루가 없습니다."),
    MEMBER_NOT_FOUND(5002, "해당 유저를 찾을 수 없습니다.");

    private final int errorCode;
    private final String errorMessage;
}
