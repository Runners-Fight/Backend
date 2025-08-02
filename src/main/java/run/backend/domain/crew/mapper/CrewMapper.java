package run.backend.domain.crew.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import run.backend.domain.crew.dto.query.CrewMemberProfileDto;
import run.backend.domain.crew.dto.query.CrewProfileDto;
import run.backend.domain.crew.dto.response.*;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;

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

    CrewMemberProfileResponse toCrewMemberProfileResponse(CrewMemberProfileDto dto);

    List<CrewMemberProfileResponse> toCrewMemberProfileResponseList(List<CrewMemberProfileDto> dtos);

    CrewSearchResponse toCrewSearchResponse(CrewProfileDto dto);

    List<CrewSearchResponse> toCrewSearchResponseList(List<CrewProfileDto> dtos);

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
