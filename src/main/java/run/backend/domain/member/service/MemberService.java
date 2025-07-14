package run.backend.domain.member.service;

import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;

public interface MemberService {

    void saveMember(Member member, MemberInfoRequest memberInfoRequest);

    MemberInfoResponse getMemberInfo(Member member);

//    void updateMember();

//    void deleteMember(Member member);
//
//    void leaveCrew(Member member, Long crewId);
//
//    void joinCrew(String crewCode);
}
