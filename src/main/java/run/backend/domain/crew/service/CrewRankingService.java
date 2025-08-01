package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.dto.response.CrewRankingResponse;
import run.backend.domain.crew.dto.response.CrewRankingStatusResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.mapper.CrewMapper;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.global.common.response.PageResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewRankingService {

    private final CrewMapper crewMapper;
    private final CrewRepository crewRepository;

    public PageResponse<CrewRankingResponse> getCrewRanking(int page, int size) {

        Page<Crew> pageResult = crewRepository.findAllByOrderByMonthlyScoreTotalDesc(PageRequest.of(page, size));
        List<CrewRankingResponse> content = crewMapper.toCrewRankingResponseList(pageResult.getContent());

        return PageResponse.toPageResponse(pageResult, content);
    }

    public CrewRankingStatusResponse getCrewRankingStatus(Crew crew) {

        int rank = 0;  // [TODO] : 스케줄링 rank 계산 구현 수정 예정

        return crewMapper.toCrewRankingStatusResponse(rank, crew);
    }
}
