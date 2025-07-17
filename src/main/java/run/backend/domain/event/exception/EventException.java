package run.backend.domain.event.exception;

import run.backend.global.exception.CustomException;

public class EventException extends CustomException {

    public EventException(final EventErrorCode eventErrorCode) {
        super(eventErrorCode);
    }

    public static class InvalidEventCreationRequest extends EventException {
        public InvalidEventCreationRequest() {
            super(EventErrorCode.RUNNING_CAPTAIN_NOT_CREW_MEMBER);
        }
    }
}
