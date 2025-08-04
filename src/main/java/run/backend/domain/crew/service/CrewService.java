package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.dto.common.CrewInviteCodeDto;
import run.backend.domain.crew.dto.query.CrewProfileDto;
import run.backend.domain.crew.dto.request.CrewInfoRequest;
import run.backend.domain.crew.dto.response.*;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.exception.CrewException;
import run.backend.domain.crew.mapper.CrewMapper;
import run.backend.domain.crew.repository.CrewRepository;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.file.service.FileService;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;
import run.backend.domain.member.repository.MemberRepository;
import run.backend.global.common.response.PageResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private final CrewMapper crewMapper;
    private final FileService fileService;
    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final JoinCrewRepository joinCrewRepository;
    private final CrewRankingService crewRankingService;

    @Transactional
    public CrewInviteCodeDto createCrew(Member member, String imageStatus, MultipartFile image, CrewInfoRequest data) {

        if (joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED))
            throw new CrewException.AlreadyJoinedCrew();

        String imageName = fileService.handleImageUpdate(imageStatus, "default-profile-image.png", image);
        Crew crew = crewMapper.toCrewEntity(imageName, data.name(), data.description());
        crewRepository.save(crew);

        JoinCrew joinCrew = JoinCrew.createLeaderJoin(member, crew);
        joinCrewRepository.save(joinCrew);

        member.updateRole(Role.LEADER);
        memberRepository.save(member);

        return new CrewInviteCodeDto(crew.getInviteCode());
    }

    @Transactional
    public void updateCrew(Crew crew, String imageStatus, MultipartFile image, CrewInfoRequest data) {

        String imageName = fileService.handleImageUpdate(imageStatus, crew.getImage(), image);
        crew.updateImage(imageName);

        if (data.name() != null) crew.updateName(data.name());
        if (data.description() != null) crew.updateDescription(data.description());

        crewRepository.save(crew);
    }

    public CrewInviteCodeDto getCrewInviteCode(Crew crew) {

        return new CrewInviteCodeDto(crew.getInviteCode());
    }

    public CrewProfileResponse getCrewByInviteCode(String inviteCode) {

        Crew crew = crewRepository.findByInviteCodeAndDeletedAtIsNull(inviteCode)
                .orElseThrow(CrewException.NotFoundCrew::new);
        Member leader = joinCrewRepository.findCrewLeader(Role.LEADER, crew);

        return crewMapper.toCrewProfile(crew, leader);
    }

    @Transactional
    public void joinCrew(Member member, Long crewId) {

        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewException.NotFoundCrew::new);
        if (joinCrewRepository.existsByMemberAndJoinStatus(member, JoinStatus.APPROVED))
            throw new CrewException.AlreadyJoinedCrew();

        JoinCrew joinCrew = JoinCrew.createAppliedJoin(member, crew);
        joinCrewRepository.save(joinCrew);
    }

    public CrewBaseInfoResponse getCrewBaseInfo(Crew crew) {

        int rank = crewRankingService.getSingleCrewRanking(crew.getId());

        return crewMapper.toCrewBaseInfo(rank, crew);
    }

    public PageResponse<CrewSearchResponse> searchCrewsByName(String crewName, int page, int size) {

        Page<CrewProfileDto> crewPage = crewRepository.findByNameContainingIgnoreCase(crewName, PageRequest.of(page, size));
        List<CrewSearchResponse> content = crewMapper.toCrewSearchResponseList(crewPage.getContent());

        return PageResponse.toPageResponse(crewPage, content);
    }
}
