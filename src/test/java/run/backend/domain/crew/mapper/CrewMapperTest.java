package run.backend.domain.crew.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import run.backend.domain.crew.dto.response.CrewProfileResponse;
import run.backend.domain.crew.dto.response.CrewRankingStatusResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CrewMapper 테스트")
public class CrewMapperTest {

    private final CrewMapper crewMapper = new CrewMapperImpl();

    private Crew crew1;

    @BeforeEach
    void setUp() {

        crew1 = new Crew(
                1L,
                "crew_name",
                "crew_description",
                "crew_image",
                "invitecode",
                2L,
                BigDecimal.valueOf(123),
                BigDecimal.valueOf(30),
                50L,
                BigDecimal.valueOf(70)
        );
    }

    @Test
    @DisplayName("정상적으로 Crew로 매핑된다")
    void toCrewEntity_mapsCorrectly() {

        // given
        String imageName = "image_url";
        String name = "crew_name";
        String description = "crew_description";

        // when
        Crew result = crewMapper.toCrewEntity(imageName, name, description);

        // then
        assertThat(result.getImage()).isEqualTo(imageName);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("정상적으로 CrewProfileResponse로 매핑된다")
    void toCrewProfileResponse_mapsCorrectly() {

        // given
        Member leader = Member.builder()
                .username("leader123")
                .nickname("철수")
                .gender(Gender.MALE)
                .age(30)
                .oauthId("oauth-123")
                .oauthType(OAuthType.GOOGLE)
                .profileImage("leader.png")
                .build();

        // when
        CrewProfileResponse result = crewMapper.toCrewProfile(crew1, leader);

        // then
        assertThat(result.crewImage()).isEqualTo(crew1.getImage());
        assertThat(result.crewName()).isEqualTo(crew1.getName());
        assertThat(result.crewDescription()).isEqualTo(crew1.getDescription());
        assertThat(result.memberCount()).isEqualTo(crew1.getMemberCount());
        assertThat(result.leaderImage()).isEqualTo(leader.getProfileImage());
        assertThat(result.leaderName()).isEqualTo(leader.getNickname());
    }

    @Test
    @DisplayName("정상적으로 CrewRankingStatusResponse로 매핑된다")
    void toCrewRankingStatusResponse_mapsCorrectly() {

        // given
        int rank = 1;

        // when
        CrewRankingStatusResponse result = crewMapper.toCrewRankingStatusResponse(rank, crew1);

        // then
        assertThat(result.ranking()).isEqualTo(1);
        assertThat(result.totalDistanceKm()).isEqualTo(123);
        assertThat(result.capturedDistanceKm()).isEqualTo(30);
    }
}
