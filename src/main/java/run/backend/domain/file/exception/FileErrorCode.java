package run.backend.domain.file.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.backend.global.exception.ErrorCode;

@Getter
@AllArgsConstructor
public enum FileErrorCode implements ErrorCode {

    FILE_UPLOAD_FAILED(4001, "파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED(4002, "파일 크기가 10MB를 초과합니다."),
    INVALID_FILE_NAME(4003, "유효하지 않은 파일명입니다."),
    INVALID_FILE_EXTENSION(4004, "지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 허용)"),
    INVALID_FILE_TYPE(4005, "이미지 파일만 업로드 가능합니다."),
    FILE_NOT_FOUND(4006, "파일을 찾을 수 없습니다."),
    FILE_DELETE_FAILED(4007, "파일 삭제에 실패했습니다.");

    private final int errorCode;
    private final String errorMessage;
}
