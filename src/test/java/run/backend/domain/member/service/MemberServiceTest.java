package run.backend.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.file.service.FileService;
import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberCrewStatusResponse;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.member.repository.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JoinCrewRepository joinCrewRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private Crew testCrew;

    private MemberInfoRequest data;

    @BeforeEach
    public void setUp() {

        // member
        testMember = spy(Member.builder()
                .username("test username")
                .oauthId("test id")
                .oauthType(OAuthType.GOOGLE)
                .profileImage("test image")
                .build());

        // crew
        testCrew = spy(Crew.builder()
                .name("test crew name")
                .description("크루 소개 테스트")
                .image("test image url")
                .build());

        // memberInfoRequest
        data = new MemberInfoRequest(
                Gender.FEMALE,
                20,
                "newNickname");
    }

    @Test
    @DisplayName("회원 정보 조회 테스트")
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

    @Test
    void getMembersCrewExists_shouldReturnNone_whenNoJoinCrew() {

        // Given
        Member member = mock(Member.class);
        when(joinCrewRepository.findByMember(member)).thenReturn(Optional.empty());

        // When
        MemberCrewStatusResponse response = memberService.getMembersCrewExists(member);

        // Then
        assertEquals("NONE", response.status());
    }

    @Test
    void getMembersCrewExists_shouldReturnJoinStatus_whenJoinCrewExists() {

        // Given
        Member member = mock(Member.class);
        JoinCrew joinCrew = mock(JoinCrew.class);
        when(joinCrew.getJoinStatus()).thenReturn(JoinStatus.APPROVED);
        when(joinCrewRepository.findByMember(member)).thenReturn(Optional.of(joinCrew));

        // When
        MemberCrewStatusResponse response = memberService.getMembersCrewExists(member);

        // Then
        assertEquals("APPROVED", response.status());
    }
}
