package run.backend.domain.crew.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import run.backend.domain.crew.dto.response.CrewRankingResponse;
import run.backend.domain.crew.dto.response.CrewRankingStatusResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.mapper.CrewMapper;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.global.common.response.PageResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Crew Ranking 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CrewRankingServiceTest {

    @InjectMocks
    private CrewRankingService crewRankingService;

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private CrewMapper crewMapper;

    private Crew crew1;
    private Crew crew2;
    private Crew crew3;
    private Crew crew4;
    private Crew crew5;

    @BeforeEach
    void setUp() {
        crew1 = Crew.builder()
                .name("crew1")
                .build();
        crew2 = Crew.builder()
                .name("crew2")
                .build();
        crew3 = Crew.builder()
                .name("crew3")
                .build();
        crew4 = Crew.builder()
                .name("crew4")
                .build();
        crew5 = Crew.builder()
                .name("crew5")
                .build();
    }

    @Nested
    @DisplayName("getCrewRanking 메서드는")
    class getCrewRankingTest {

        @Test
        @DisplayName("페이지 정보를 기반으로 Crew 랭킹 응답을 반환한다")
        void getCrewRanking_whenValidPageRequest_thenReturnsPageResponse() {

            // given
            int page = 0;
            int size = 5;

            List<Crew> crews = List.of(crew1, crew2, crew3, crew4, crew5);
            Page<Crew> crewPage = new PageImpl<>(crews, PageRequest.of(page, size), crews.size());

            List<CrewRankingResponse> responseList = List.of(
                    new CrewRankingResponse(1L, "name1", "image1", 5),
                    new CrewRankingResponse(2L, "name2", "image2", 4),
                    new CrewRankingResponse(3L, "name3", "image3", 3),
                    new CrewRankingResponse(4L, "name4", "image4", 2),
                    new CrewRankingResponse(5L, "name5", "image5", 1)
            );

            when(crewRepository.findAllByDeletedAtIsNullOrderByMonthlyScoreTotalDesc(PageRequest.of(page, size)))
                    .thenReturn(crewPage);
            when(crewMapper.toCrewRankingResponseList(crewPage.getContent()))
                    .thenReturn(responseList);

            // when
            PageResponse<CrewRankingResponse> result = crewRankingService.getCrewRanking(page, size);

            // then
            assertThat(result.curPage()).isEqualTo(0);
            assertThat(result.curElements()).isEqualTo(5);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.totalElements()).isEqualTo(5);
            assertThat(result.isLast()).isTrue();
        }
    }

    @Nested
    @DisplayName("getCrewRankingStatus 메서드는")
    class getCrewRankingStatusTest {

        @Test
        @DisplayName("정상적으로 Crew 랭킹 상태를 반환한다")
        void getCrewRankingStatus_whenValidCrewGiven_thenReturnsStatusResponse() {

            // given
            int rank = 0;
            CrewRankingStatusResponse expectedResponse = new CrewRankingStatusResponse(rank, 0, 0);

            when(crewMapper.toCrewRankingStatusResponse(rank, crew1))
                    .thenReturn(expectedResponse);

            // when
            CrewRankingStatusResponse result = crewRankingService.getCrewRankingStatus(crew1);

            // then
            assertThat(result).isEqualTo(expectedResponse);
        }
    }
}
