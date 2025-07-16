package run.backend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.file.service.FileService;
import run.backend.domain.member.dto.request.MemberInfoRequest;
import run.backend.domain.member.dto.response.MemberInfoResponse;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Override
    public MemberInfoResponse getMemberInfo(Member member) {

        String crewName = memberRepository.findCrewByMemberIdAndStatus(member.getId(), JoinStatus.APPROVED)
                .map(Crew::getName)
                .orElse("N/A");
        return new MemberInfoResponse(member.getProfileImage(), member.getNickname(), crewName);
    }

    @Override
    @Transactional
    public void updateMemberInfo(Member member, String imageStatus, MultipartFile image, MemberInfoRequest data) {

        switch (imageStatus) {

            case "updated" :
                fileService.deleteImage(member.getProfileImage());   // 기존 이미지 지우기
                String newImageName = fileService.saveProfileImage(image);   // 새로운 이미지 저장
                member.updateImage(newImageName);
                break ;
            case "removed" :
                fileService.deleteImage(member.getProfileImage());
                member.updateImage("default-profile-image.png");
                break ;
        }

        if (data.gender() != null)
            member.updateGender(data.gender());
        if (data.age() != null)
            member.updateAge(data.age());
        if (data.nickname() != null)
            member.updateNickname(data.nickname());

        memberRepository.save(member);
    }
}
