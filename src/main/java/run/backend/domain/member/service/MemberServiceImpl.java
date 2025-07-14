package run.backend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberInfoResponse getMemberInfo(Member member) {

        String crewName = memberRepository.findCrewByMemberIdAndStatus(member.getId(), JoinStatus.APPROVED)
                .map(Crew::getName)
                .orElse("N/A");

        return new MemberInfoResponse(member.getProfileImage(), member.getNickname(), crewName);
    }
}
