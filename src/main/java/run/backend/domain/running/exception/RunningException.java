package run.backend.domain.running.exception;

import run.backend.global.exception.CustomException;

public class RunningException extends CustomException {

    public RunningException(final RunningErrorCode runningErrorCode) {
        super(runningErrorCode);
    }

    public static class InvalidCoordinate extends CustomException {
        public InvalidCoordinate() {
            super(RunningErrorCode.INVALID_COORDINATE);
        }
    }
}
