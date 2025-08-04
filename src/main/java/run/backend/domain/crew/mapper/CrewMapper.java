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

    @Mapping(target = "crewId", source = "crew.id")
    @Mapping(target = "monthlyScoreTotal", expression = "java(crew.getMonthlyScoreTotal().intValue())")
    CrewRankingResponse toCrewRankingResponse(Crew crew);

    List<CrewRankingResponse> toCrewRankingResponseList(List<Crew> crews);

    CrewMemberProfileResponse toCrewMemberProfileResponse(CrewMemberProfileDto dto);

    List<CrewMemberProfileResponse> toCrewMemberProfileResponseList(List<CrewMemberProfileDto> dtos);

    CrewSearchResponse toCrewSearchResponse(CrewProfileDto dto);

    List<CrewSearchResponse> toCrewSearchResponseList(List<CrewProfileDto> dtos);

    @Mapping(target = "image", source = "imageName")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    Crew toCrewEntity(String imageName, String name, String description);

    @Mapping(target = "crewImage", source = "crew.image")
    @Mapping(target = "crewName", source = "crew.name")
    @Mapping(target = "crewDescription", source = "crew.description")
    @Mapping(target = "memberCount", source = "crew.memberCount")
    @Mapping(target = "leaderImage", source = "leader.profileImage")
    @Mapping(target = "leaderName", source = "leader.nickname")
    CrewProfileResponse toCrewProfile(Crew crew, Member leader);

    @Mapping(target = "ranking", source = "rank")
    @Mapping(target = "totalDistanceKm", expression = "java(crew.getMonthlyDistanceTotal().intValue())")
    @Mapping(target = "capturedDistanceKm", expression = "java(crew.getCapturedDistanceTotal().intValue())")
    CrewRankingStatusResponse toCrewRankingStatusResponse(int rank, Crew crew);
}
