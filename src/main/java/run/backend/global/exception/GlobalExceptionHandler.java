package run.backend.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import run.backend.domain.auth.exception.AuthException;
import run.backend.domain.crew.exception.CrewException;
import run.backend.domain.event.exception.EventException;
import run.backend.domain.file.exception.FileException;
import run.backend.domain.member.exception.MemberException;
import run.backend.global.common.response.CommonResponse;
import run.backend.global.exception.httpError.HttpErrorCode;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler({
        AuthException.RefreshTokenNotFound.class,
        FileException.FileNotFound.class,
        MemberException.MemberNotJoinedCrew.class,
        MemberException.MemberNotFound.class,
        CrewException.NotFoundCrew.class,
        EventException.EventNotFound.class
    })
    public ResponseEntity<CommonResponse<Void>> handleNotFound(final CustomException e) {

        log.warn("[NOT_FOUND_EXCEPTION] {}", e.toString());

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new CommonResponse<>(e.getErrorCode(), e.getErrorMessage()));
    }

    @ExceptionHandler({
        AuthException.UserAlreadyExists.class,
        FileException.FileSizeExceeded.class,
        FileException.InvalidFileName.class,
        FileException.InvalidFileExtension.class,
        FileException.InvalidFileType.class,
        CrewException.AlreadyJoinedCrew.class
    })
    public ResponseEntity<CommonResponse<Void>> handleConflict(final CustomException e) {

        log.warn("[CONFLICT_EXCEPTION] {}", e.toString());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new CommonResponse<>(e.getErrorCode(), e.getErrorMessage()));
    }

    @ExceptionHandler({
        AuthException.InvalidSignupToken.class,
        AuthException.OauthRequestFailed.class,
        AuthException.TokenMissingAuthority.class,
        AuthException.InvalidRefreshToken.class,
        AuthException.RefreshTokenExpired.class,
        FileException.FileUploadFailed.class,
        EventException.InvalidEventCreationRequest.class
    })
    public ResponseEntity<CommonResponse<Void>> handleBadRequest(final CustomException e) {

        log.warn("[BAD_REQUEST_EXCEPTION] {}", e.toString());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new CommonResponse<>(e.getErrorCode(), e.getErrorMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleUnknownException(final Exception e) {

        log.error("[INTERNAL_SERVER_ERROR]", e);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new CommonResponse<>(HttpErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
                HttpErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage()));
    }
}
