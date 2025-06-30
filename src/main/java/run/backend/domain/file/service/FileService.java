package run.backend.domain.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import run.backend.global.exception.ApplicationException;
import run.backend.global.exception.ExceptionCode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png",
        "gif");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "default-profile-image.png";
        }

        validateFile(file);

        try {
            Path uploadPath = Paths.get(uploadDir, "profiles");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;

        } catch (IOException e) {
            throw new ApplicationException(ExceptionCode.FILE_UPLOAD_FAILED);
        }
    }

    public Resource getFileResource(String filename) {
        validateFilename(filename);

        try {
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = basePath.resolve("profiles").resolve(filename);

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ApplicationException(ExceptionCode.FILE_NOT_FOUND);
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new ApplicationException(ExceptionCode.FILE_NOT_FOUND);
        }
    }

    public String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ApplicationException(ExceptionCode.FILE_SIZE_EXCEEDED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApplicationException(ExceptionCode.INVALID_FILE_TYPE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ApplicationException(ExceptionCode.INVALID_FILE_NAME);
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ApplicationException(ExceptionCode.INVALID_FILE_EXTENSION);
        }
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new ApplicationException(ExceptionCode.INVALID_FILE_NAME);
        }

        if (!filename.matches("^[a-fA-F0-9-]+\\.(jpg|jpeg|png|gif)$")) {
            throw new ApplicationException(ExceptionCode.INVALID_FILE_NAME);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
