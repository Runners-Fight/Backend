package run.backend.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.member.repository.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member testMember;
    private Crew testCrew;

    @BeforeEach
    public void setUp() {

        // member
        testMember = Member.builder()
                .username("test username")
                .oauthId("test id")
                .oauthType(OAuthType.GOOGLE)
                .profileImage("test image")
                .build();

        // crew
        testCrew = Crew.builder()
                .name("test crew name")
                .description("크루 소개 테스트")
                .image("test image url")
                .build();
    }

    @Test
    @DisplayName("회원 정보가 올바르게 변경 되었는지 확인")
    public void saveMemberInfoTest() {

        // given
        MemberInfoRequest request = new MemberInfoRequest(
              Gender.FEMALE,
              24,
              "test nickname"
        );

        // when
        memberService.saveMember(testMember, request);

        // then
        assertEquals(Gender.FEMALE, testMember.getGender());
        assertEquals(24, testMember.getAge());
        assertEquals("test nickname", testMember.getNickname());
    }

    @Test
    @DisplayName("회원 정보가 올바르게 조회 되는지 확인")
    public void getMemberInfoTest() {

        // given
        when(memberRepository.findCrewByMemberIdAndStatus(testMember.getId(), JoinStatus.APPROVED))
                .thenReturn(Optional.of(testCrew));

        // when
        MemberInfoResponse response = memberService.getMemberInfo(testMember);

        // then
        assertEquals(testMember.getNickname(), response.nickName());
        assertEquals(testCrew.getName(), response.crewName());
    }
}
