package run.backend.domain.crew.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("JoinCrew 도메인 테스트")
public class JoinCrewTest {

    Member member = mock(Member.class);
    Crew crew = mock(Crew.class);

    @Test
    @DisplayName("리더가 JoinCrew 생성 시 role은 LEADER, 상태는 APPROVED, joinedDate는 오늘이다.")
    void createLeaderJoin_setsCorrectValues() {

        // when
        JoinCrew joinCrew = JoinCrew.createLeaderJoin(member, crew);

        // then
        assertThat(joinCrew.getMember()).isEqualTo(member);
        assertThat(joinCrew.getCrew()).isEqualTo(crew);
        assertThat(joinCrew.getJoinStatus()).isEqualTo(JoinStatus.APPROVED);
        assertThat(joinCrew.getJoinedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("일반 회원이 JoinCrew 생성 시 role은 MEMBER, 상태는 APPLIED, joinedDate는 null이다.")
    void createAppliedJoin_setsCorrectValues() {

        // when
        JoinCrew joinCrew = JoinCrew.createAppliedJoin(member, crew);

        // then
        assertThat(joinCrew.getMember()).isEqualTo(member);
        assertThat(joinCrew.getCrew()).isEqualTo(crew);
        assertThat(joinCrew.getJoinStatus()).isEqualTo(JoinStatus.APPLIED);
        assertThat(joinCrew.getJoinedDate()).isNull();
    }
}
