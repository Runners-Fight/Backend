package run.backend.domain.crew.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import run.backend.domain.crew.dto.response.*;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CrewMapper {

    @Mapping(target = "rank", source = "rank")
    CrewBaseInfoResponse toCrewBaseInfo(int rank, Crew crew);

    @Mapping(target = "monthlyScoreTotal", expression = "java(crew.getMonthlyScoreTotal().intValue())")
    CrewRankingResponse toCrewRankingResponse(Crew crew);

    List<CrewRankingResponse> toCrewRankingResponseList(List<Crew> crews);

    @Mapping(source = "profileImage", target = "image")
    CrewMemberProfileResponse toCrewMemberProfile(Member member);

    default CrewMemberResponse toCrewMemberResponse(List<Member> crewMembers) {
        List<CrewMemberProfileResponse> managers = new ArrayList<>();
        List<CrewMemberProfileResponse> members = new ArrayList<>();

        for (Member member : crewMembers) {
            CrewMemberProfileResponse profile = toCrewMemberProfile(member);

            if (member.getRole() == Role.MEMBER) {
                members.add(profile);
            } else {
                managers.add(profile);
            }
        }

        return new CrewMemberResponse(managers, members);
    }

    default Crew toEntity(String imageName, String name, String description) {
        return Crew.builder()
                .image(imageName)
                .name(name)
                .description(description)
                .build();
    }

    default CrewProfileResponse toCrewProfile(Crew crew, Member leader) {
        return CrewProfileResponse.builder()
                .crewImage(crew.getImage())
                .crewName(crew.getName())
                .crewDescription(crew.getDescription())
                .memberCount(crew.getMemberCount())
                .leaderImage(leader.getProfileImage())
                .leaderName(leader.getNickname())
                .build();
    }

    default CrewRankingStatusResponse toCrewRankingStatusResponse(
            int rank,
            Crew crew
    ) {
        return new CrewRankingStatusResponse(
                rank,
                crew.getMonthlyDistanceTotal().intValue(),
                crew.getCapturedDistanceTotal().intValue()
        );
    }
}
