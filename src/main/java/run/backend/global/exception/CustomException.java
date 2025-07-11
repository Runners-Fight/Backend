package run.backend.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final int errorCode;
    private final String errorMessage;

    public <T extends Enum<T> & ErrorCode> CustomException(final T errorType) {
        errorCode = errorType.getErrorCode();
        errorMessage = errorType.getErrorMessage();
    }

    @Override
    public String toString() {

        return String.format("errorCode=%d | errorMessage=%s",
                errorCode,
                errorMessage );
    }
}
