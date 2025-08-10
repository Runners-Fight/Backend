package run.backend.domain.notification.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    INVALID_NOTIFICATION_TYPE(6001, "유효하지 않은 알림 타입입니다.");

    private final int errorCode;
    private final String errorMessage;
}
