package run.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationException extends RuntimeException {

    public ExceptionCode exceptionCode;

    @Override
    public String toString() {
        return exceptionCode.getMessage();
    }
}
