package run.backend.domain.crew.exception;

import run.backend.global.exception.CustomException;

public class CrewException extends CustomException {

    public CrewException(final CrewErrorCode crewErrorCode) {
        super(crewErrorCode);
    }

    public static class AlreadyJoinedCrew extends CrewException {
        public AlreadyJoinedCrew() {
            super(CrewErrorCode.ALREADY_JOINED_CREW);
        }
    }

    public static class NotFoundCrew extends CrewException {
        public NotFoundCrew() {
            super(CrewErrorCode.NOT_FOUND_CREW);
        }
    }
}
