package run.backend.domain.crew.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.common.CrewInviteCodeDto;
import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.exception.CrewException;
import run.backend.domain.crew.mapper.CrewMapper;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.file.service.FileService;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.repository.MemberRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@DisplayName("Crew 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CrewServiceTest {

    @InjectMocks
    private CrewService crewService;

    @Mock
    private FileService fileService;

    @Mock
    private JoinCrewRepository joinCrewRepository;

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CrewMapper crewMapper;

    private Crew crew;

    private Member member;
    private CrewInfoRequest request;

    @BeforeEach
    void setUp() {
        member = Member.builder().username("테스트 유저").build();
        request = new CrewInfoRequest("러너스", "러너스 크루입니다.");
        crew = Crew.builder()
                .name("테스트 크루")
                .description("테스트 설명")
                .image("default-profile-image.png")
                .build();
    }

    @Nested
    @DisplayName("createCrew 메서드는")
    class createCrewTest {

        @Test
        @DisplayName("이미 크루에 가입된 회원이 크루 생성을 시도하면 예외가 발생한다.")
        void throwsException_whenMemberAlreadyInCrew() {

            // given
            when(joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED))
                    .thenReturn(true);

            // when + then
            assertThatThrownBy(() ->
                    crewService.createCrew(member, "unchanged", null, request))
                    .isInstanceOf(CrewException.AlreadyJoinedCrew.class);
        }

        @Test
        @DisplayName("크루 생성 시 응답으로 invite-code가 포함되어야 한다.")
        void respondsWithInviteCode_whenCreatingCrew() {

            // given
            when(joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED))
                    .thenReturn(false);
            when(fileService.handleImageUpdate(eq("unchanged"), eq("default-profile-image.png"), isNull()))
                    .thenReturn("default-profile-image.png");
            when(crewMapper.toCrewEntity(eq("default-profile-image.png"), eq(request.name()), eq(request.description())))
                    .thenReturn(crew);
            when(crewRepository.save(any(Crew.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(joinCrewRepository.save(any(JoinCrew.class))).thenReturn(null);
            when(memberRepository.save(any())).thenReturn(member);

            // when
            CrewInviteCodeDto response = crewService.createCrew(member, "unchanged", null, request);

            // then
            assertThat(response.inviteCode()).isNotNull();
        }

        @Test
        @DisplayName("크루 생성 시 joinCrew가 저장된다.")
        void saveJoinCrew_whenCreatingCrew() {

            // given
            when(joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED)).thenReturn(false);
            when(fileService.handleImageUpdate(eq("unchanged"), eq("default-profile-image.png"), isNull()))
                    .thenReturn("default-profile-image.png");
            when(crewMapper.toCrewEntity(eq("default-profile-image.png"), eq(request.name()), eq(request.description())))
                    .thenReturn(crew);
            when(crewRepository.save(any(Crew.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(joinCrewRepository.save(any(JoinCrew.class))).thenReturn(null);
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // when
            crewService.createCrew(member, "unchanged", null, request);

            // then
            verify(joinCrewRepository).save(any(JoinCrew.class));
        }

        @Test
        @DisplayName("크루 생성 시 생성자의 역할이 LEADER로 변경해서 저장한다.")
        void updatesMemberRoleToLeader_whenCreatingCrew() {

            // given
            when(joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED)).thenReturn(false);
            when(fileService.handleImageUpdate(eq("unchanged"), eq("default-profile-image.png"), isNull()))
                    .thenReturn("default-profile-image.png");
            when(crewMapper.toCrewEntity(eq("default-profile-image.png"), eq(request.name()), eq(request.description())))
                    .thenReturn(crew);
            when(crewRepository.save(any(Crew.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(joinCrewRepository.save(any(JoinCrew.class))).thenReturn(null);
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // when
            crewService.createCrew(member, "unchanged", null, request);

            // then
            verify(memberRepository).save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("updateCrew 메서드는")
    class updateCrewTest {

        @Test
        @DisplayName("imageStatus가 updated인 경우 기존 이미지를 삭제하고 새 이미지를 저장한다.")
        void updateImage_whenImageStatusIsUpdated() {

            // given
            String oldImageName = "old_image.png";
            String newImageName = "new_image.png";
            MultipartFile mockFile = mock(MultipartFile.class);
            Crew crew = mock(Crew.class);

            when(crew.getImage()).thenReturn(oldImageName);
            when(fileService.handleImageUpdate(eq("updated"), eq(oldImageName), eq(mockFile)))
                    .thenReturn(newImageName);

            // when
            crewService.updateCrew(crew, "updated", mockFile, request);

            // then
            verify(fileService).handleImageUpdate(eq("updated"), eq(oldImageName), eq(mockFile));
            verify(crew).updateImage(newImageName);
            verify(crewRepository).save(crew);
        }

        @Test
        @DisplayName("imageStatus가 removed인 경우 기존 이미지를 삭제하고 기본 이미지를 저장한다.")
        void removeImage_whenImageStatusIsRemoved() {

            // given
            String oldImageName = "old_image.png";
            MultipartFile mockFile = mock(MultipartFile.class);
            Crew crew = mock(Crew.class);

            when(crew.getImage()).thenReturn(oldImageName);
            when(fileService.handleImageUpdate(eq("removed"), eq(oldImageName), eq(mockFile)))
                    .thenReturn("default-profile-image.png");

            // when
            crewService.updateCrew(crew, "removed", mockFile, request);

            // then
            verify(fileService).handleImageUpdate(eq("removed"), eq(oldImageName), eq(mockFile));
            verify(crew).updateImage("default-profile-image.png");
            verify(crewRepository).save(crew);
        }

        @Test
        @DisplayName("name이 null이 아니면 이름을 업데이트한다.")
        void updateName_whenNameIsNotNull() {

            // given
            Crew crew = mock(Crew.class);

            // when
            crewService.updateCrew(crew, "unchanged", null, request);

            // then
            verify(crew).updateName("러너스");
            verify(crewRepository).save(crew);

        }

        @Test
        @DisplayName("description null이 아니면 설명을 업데이트한다.")
        void updateDescription_whenDescriptionIsNotNull() {

            // given
            Crew crew = mock(Crew.class);

            // when
            crewService.updateCrew(crew, "unchanged", null, request);

            // then
            verify(crew).updateDescription("러너스 크루입니다.");
            verify(crewRepository).save(crew);
        }
    }

    @Nested
    @DisplayName("joinCrew 메서드는")
    class joinCrewTest {

        @Test
        @DisplayName("존재하지 않는 crew id이면 예외가 발생한다.")
        void throwException_whenCrewNotFound() {

            // given
            Long crewId = 2L;
            when(crewRepository.findById(crewId)).thenReturn(Optional.empty());

            // when + then
            assertThatThrownBy(() -> crewService.joinCrew(member, crewId))
                    .isInstanceOf(CrewException.NotFoundCrew.class);
        }

        @Test
        @DisplayName("이미 크루에 가입한 회원이 크루 가입 시도를 하면 예외가 발생한다.")
        void throwException_whenMemberAlreadyJoinedCrew() {

            // given
            Long crewId = 2L;
            when(crewRepository.findById(crewId)).thenReturn(Optional.of(crew));
            when(joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED))
                    .thenReturn(true);

            // when + then
            assertThatThrownBy(() -> crewService.joinCrew(member, crewId))
                    .isInstanceOf(CrewException.AlreadyJoinedCrew.class);
        }

        @Test
        @DisplayName("정상적으로 crew에 가입 신청을 저장한다.")
        void saveJoinCrew_whenValidCrewIdGiven() {

            // given
            Long crewId = 1L;
            when(crewRepository.findById(crewId)).thenReturn(Optional.of(crew));
            when(joinCrewRepository.save(any(JoinCrew.class))).thenReturn(null);

            // when
            crewService.joinCrew(member, crewId);

            // then
            verify(crewRepository).findById(crewId);
            verify(joinCrewRepository).save(any(JoinCrew.class));
        }
    }
}
