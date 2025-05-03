package run.backend.domain.member.service;

import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberInfoResponse;

public interface MemberService {

    void updateMember(Long memberId, MemberInfoRequest memberInfoRequest);

    MemberInfoResponse getMemberInfo(Long memberId);

    void deleteMember(Long memberId);

    void leaveCrew(Long memberId, Long crewId);

    void joinCrew(String crewCode);
}
