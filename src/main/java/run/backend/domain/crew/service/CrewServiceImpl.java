package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.common.CrewInviteCodeDto;
import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.dto.response.CrewProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.exception.CrewException;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.file.service.FileService;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;
import run.backend.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewServiceImpl implements CrewService {

    private final FileService fileService;
    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final JoinCrewRepository joinCrewRepository;

    @Override
    @Transactional
    public CrewInviteCodeDto createCrew(Member member, String imageStatus, MultipartFile image, CrewInfoRequest data) {

        if (joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED))
            throw new CrewException.AlreadyJoinedCrew();

        // 1. Crew 생성
        String imageName = "default-profile-image.png";
        if (imageStatus.equals("updated"))
            imageName = fileService.saveProfileImage(image);
        Crew crew = Crew.builder()
                .image(imageName)
                .name(data.name())
                .description(data.description())
                .build();
        crewRepository.save(crew);

        // 2. JoinCrew 생성
        JoinCrew joinCrew = JoinCrew.createLeaderJoin(member, crew);
        joinCrewRepository.save(joinCrew);

        // 3. Member Role LEADER 로 변경
        member.updateRole(Role.LEADER);
        memberRepository.save(member);

        return new CrewInviteCodeDto(crew.getInviteCode());
    }

    @Override
    @Transactional
    public void updateCrew(Member member, Crew crew, String imageStatus, MultipartFile image, CrewInfoRequest data) {

        switch (imageStatus) {

            case "updated" :
                fileService.deleteImage(crew.getImage());
                String newImageName = fileService.saveProfileImage(image);
                crew.updateImage(newImageName);
                break;
            case "removed" :
                fileService.deleteImage(crew.getImage());
                crew.updateImage("default-profile-image.png");
                break;
        }

        if (data.name() != null)
            crew.updateName(data.name());
        if (data.description() != null)
            crew.updateDescription(data.description());

        crewRepository.save(crew);
    }

    @Override
    public CrewInviteCodeDto getCrewInviteCode(Crew crew) {

        return new CrewInviteCodeDto(crew.getInviteCode());
    }

    @Override
    public CrewProfileResponse getCrewByInviteCode(String inviteCode) {

        Crew crew = crewRepository.findByInviteCode(inviteCode)
                .orElseThrow(CrewException.NotFoundCrew::new);
        Member leader = joinCrewRepository.findCrewLeader(Role.LEADER, crew);

        return CrewProfileResponse.builder()
                .crewImage(crew.getImage())
                .crewName(crew.getName())
                .crewDescription(crew.getDescription())
                .memberCount(crew.getMemberCount())
                .leaderImage(leader.getProfileImage())
                .leaderName(leader.getNickname())
                .build();
    }

    @Override
    @Transactional
    public void joinCrew(Member member, Long crewId) {

        Crew crew = crewRepository.findById(crewId)
                .orElseThrow();

        JoinCrew joinCrew = JoinCrew.createAppliedJoin(member, crew);
        joinCrewRepository.save(joinCrew);
    }
}
