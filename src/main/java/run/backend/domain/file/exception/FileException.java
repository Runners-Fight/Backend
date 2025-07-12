package run.backend.domain.file.exception;

import run.backend.global.exception.CustomException;

public class FileException extends CustomException {

    public FileException(final FileErrorCode fileErrorCode) {
        super(fileErrorCode);
    }

    public static class FileUploadFailed extends FileException {
        public FileUploadFailed() {
            super(FileErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public static class FileSizeExceeded extends FileException {
        public FileSizeExceeded() {
            super(FileErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    public static class InvalidFileName extends FileException {
        public InvalidFileName() {
            super(FileErrorCode.INVALID_FILE_NAME);
        }
    }

    public static class InvalidFileExtension extends FileException {
        public InvalidFileExtension() {
            super(FileErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    public static class InvalidFileType extends FileException {
        public InvalidFileType() {
            super(FileErrorCode.INVALID_FILE_TYPE);
        }
    }

    public static class FileNotFound extends FileException {
        public FileNotFound() {
            super(FileErrorCode.FILE_NOT_FOUND);
        }
    }
}
