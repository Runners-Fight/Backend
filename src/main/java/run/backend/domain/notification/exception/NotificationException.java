package run.backend.domain.notification.exception;

import run.backend.global.exception.CustomException;

public class NotificationException extends CustomException {

    public NotificationException(final NotificationErrorCode notificationErrorCode) {
        super(notificationErrorCode);
    }

    public static class InvalidNotificationType extends NotificationException {
        public InvalidNotificationType() {
            super(NotificationErrorCode.INVALID_NOTIFICATION_TYPE);
        }
    }

    public static class NotificationNotFound extends NotificationException {
        public NotificationNotFound() {
            super(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }
    }
}
