package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.dto.response.CrewRankingResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.global.common.response.PageResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewRankingService {

    private final CrewRepository crewRepository;

    public PageResponse<CrewRankingResponse> getCrewRanking(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Crew> pageResult = crewRepository.findAllByOrderByMonthlyScoreTotalDesc(pageable);

        List<CrewRankingResponse> content = pageResult.stream()
                .map(crew -> new CrewRankingResponse(
                        crew.getId(),
                        crew.getName(),
                        crew.getImage(),
                        crew.getMonthlyDistanceTotal().intValue()
                ))
                .toList();

        return new PageResponse<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.isLast(),
                content
        );
    }
}
