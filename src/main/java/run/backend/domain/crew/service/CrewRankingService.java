package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.dto.response.CrewRankingResponse;
import run.backend.domain.crew.dto.response.CrewRankingStatusResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.exception.CrewException;
import run.backend.domain.crew.mapper.CrewMapper;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.global.common.response.PageResponse;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewRankingService {

    private final CrewMapper crewMapper;
    private final CrewRepository crewRepository;

    public PageResponse<CrewRankingResponse> getCrewRanking(int page, int size) {

        Page<Crew> pageResult = crewRepository.findAllByDeletedAtIsNullOrderByMonthlyScoreTotalDesc(PageRequest.of(page, size));
        List<CrewRankingResponse> content = crewMapper.toCrewRankingResponseList(pageResult.getContent());

        return PageResponse.toPageResponse(pageResult, content);
    }

    public CrewRankingStatusResponse getCrewRankingStatus(Crew crew) {

        int rank = getSingleCrewRanking(crew.getId());

        return crewMapper.toCrewRankingStatusResponse(rank, crew);
    }

    public int getSingleCrewRanking(Long crewId) {

        List<Long> crewIds = crewRepository.findAllActiveCrewIdsOrderByScoreDesc();

        return IntStream.range(0, crewIds.size())
                .filter(i -> crewIds.get(i).equals(crewId))
                .findFirst()
                .orElseThrow(CrewException.NotFoundCrew::new)
                + 1;
    }
}
