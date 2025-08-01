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

    public static class EventNotFound extends EventException {
        public EventNotFound() {
            super(EventErrorCode.EVENT_NOT_FOUND);
        }
    }

    public static class AlreadyJoinedEvent extends EventException {
        public AlreadyJoinedEvent() {
            super(EventErrorCode.ALREADY_JOINED_EVENT);
        }
    }

    public static class JoinEventNotFound extends EventException {
        public JoinEventNotFound() {
            super(EventErrorCode.JOIN_EVENT_NOT_FOUND);
        }
    }

}
