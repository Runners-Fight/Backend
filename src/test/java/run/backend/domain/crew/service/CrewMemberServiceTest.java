package run.backend.domain.crew.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import run.backend.domain.crew.dto.query.CrewMemberProfileDto;
import run.backend.domain.crew.dto.request.MemberRoleChangeRequest;
import run.backend.domain.crew.dto.response.CrewMemberProfileResponse;
import run.backend.domain.crew.dto.response.CrewMemberResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.mapper.CrewMapper;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;
import run.backend.domain.member.exception.MemberException;
import run.backend.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CrewMember 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CrewMemberServiceTest {

    @InjectMocks
    private CrewMemberService crewMemberService;

    @Mock
    private CrewMapper crewMapper;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JoinCrewRepository joinCrewRepository;

    @Mock
    private Member member;

    @Mock
    private Crew crew;


    @Nested
    @DisplayName("getCrewMembers 메서드는 ")
    class getCrewMembersTest {

        @Test
        @DisplayName("크루원 조회 시 역할에 따라 관리자와 일반 멤버로 구분하여 응답한다.")
        void returnsCrewMembersGroupedByRole() {

            // given
            List<CrewMemberProfileDto> dtos = List.of(
                    new CrewMemberProfileDto("img1", "user1", Role.MANAGER),
                    new CrewMemberProfileDto("img2", "user2", Role.MEMBER)
            );
            List<CrewMemberProfileResponse> responses = List.of(
                    new CrewMemberProfileResponse("img1", "user1", Role.MANAGER),
                    new CrewMemberProfileResponse("img2", "user2", Role.MEMBER)
            );
            when(joinCrewRepository.findAllCrewMemberByCrewId(crew.getId(), JoinStatus.APPROVED))
                    .thenReturn(dtos);
            when(crewMapper.toCrewMemberProfileResponseList(dtos))
                    .thenReturn(responses);

            // when
            CrewMemberResponse result = crewMemberService.getCrewMembers(crew);

            // then
            assertEquals(1, result.managers().size());
            assertEquals(1, result.members().size());
            assertEquals("user1", result.managers().get(0).nickname());
            assertEquals("user2", result.members().get(0).nickname());
        }
    }

    @Nested
    @DisplayName("updateCrewMemberRole 메서드는 ")
    class updateCrewMemberRoleTest {

        @Test
        @DisplayName("회원 역할 변경 시 해당 회원의 역할을 요청된 값으로 변경한다.")
        void updatesMemberRole_whenMemberExists() {

            // given
            MemberRoleChangeRequest request = new MemberRoleChangeRequest(Role.MANAGER);
            when(memberRepository.findById(member.getId()))
                    .thenReturn(Optional.of(member));

            // when
            crewMemberService.updateCrewMemberRole(member.getId(), request);

            // then
            verify(member).updateRole(Role.MANAGER);
            verify(memberRepository).save(member);
        }

        @Test
        @DisplayName("존재하지 않는 회원에게 역할 변경을 시도하면 예외가 발생한다.")
        void throwsException_whenMemberNotFound() {

            // given
            Long memberId = 999L;
            MemberRoleChangeRequest request = new MemberRoleChangeRequest(Role.MANAGER);

            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // then
            assertThrows(MemberException.MemberNotFound.class,
                    () -> crewMemberService.updateCrewMemberRole(memberId, request));
        }
    }
}
