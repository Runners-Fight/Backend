package run.backend.domain.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewMemberService {

    private final CrewMapper crewMapper;
    private final MemberRepository memberRepository;
    private final JoinCrewRepository joinCrewRepository;

    public CrewMemberResponse getCrewMembers(Crew crew) {

        List<CrewMemberProfileDto> dtos = joinCrewRepository.findAllCrewMemberByCrewId(crew.getId(), JoinStatus.APPROVED);

        List<CrewMemberProfileResponse> all = crewMapper.toCrewMemberProfileResponseList(dtos);

        List<CrewMemberProfileResponse> managers = new ArrayList<>();
        List<CrewMemberProfileResponse> members = new ArrayList<>();

        for (CrewMemberProfileResponse profile : all) {

            if (profile.role() == Role.MEMBER)
                members.add(profile);
            else
                managers.add(profile);
        }
        return new CrewMemberResponse(managers, members);
    }

    @Transactional
    public void updateCrewMemberRole(Long memberId, MemberRoleChangeRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.MemberNotFound::new);

        member.updateRole(request.role());
        memberRepository.save(member);
    }
}
