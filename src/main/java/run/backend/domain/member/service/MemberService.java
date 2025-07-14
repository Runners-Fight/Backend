package run.backend.domain.member.service;

import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;

public interface MemberService {

    MemberInfoResponse getMemberInfo(Member member);

    void updateMemberInfo(Member member, String imageStatus, MultipartFile image, MemberInfoRequest data);

//    void updateMember();

//    void deleteMember(Member member);
//
//    void leaveCrew(Member member, Long crewId);
//
//    void joinCrew(String crewCode);
}
