package run.backend.domain.crew.mapper;

import org.springframework.stereotype.Component;
import run.backend.domain.crew.dto.response.CrewBaseInfoResponse;
import run.backend.domain.crew.dto.response.CrewProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;

@Component
public class CrewMapper {

    public CrewProfileResponse toCrewProfile(Crew crew, Member leader) {
        return CrewProfileResponse.builder()
                .crewImage(crew.getImage())
                .crewName(crew.getName())
                .crewDescription(crew.getDescription())
                .memberCount(crew.getMemberCount())
                .leaderImage(leader.getProfileImage())
                .leaderName(leader.getNickname())
                .build();
    }

    public CrewBaseInfoResponse toCrewBaseInfo(int rank, Crew crew) {
        return CrewBaseInfoResponse.builder()
                .rank(rank)
                .image(crew.getImage())
                .name(crew.getName())
                .description(crew.getDescription())
                .memberCount(crew.getMemberCount())
                .monthlyDistanceTotal(crew.getMonthlyDistanceTotal())
                .monthlyTimeTotal(crew.getMonthlyTimeTotal())
                .build();
    }
}
