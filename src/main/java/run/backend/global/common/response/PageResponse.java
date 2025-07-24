package run.backend.global.common.response;

import java.util.List;

public record PageResponse<T>(
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean isLast,
        List<T> content
) {}