package run.backend.global.common.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean isLast,
        List<T> content
) {

    public static <T> PageResponse<T> toPageResponse(Page<?> page, List<T> content) {
        return new PageResponse<>(
                page.getNumber(),
                content.size(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                content
        );
    }
}