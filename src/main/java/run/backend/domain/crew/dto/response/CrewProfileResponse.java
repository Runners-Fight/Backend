package run.backend.domain.crew.dto.response;

import lombok.Builder;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;

@Builder
public record CrewProfileResponse(
        String crewImage,
        String crewName,
        String crewDescription,
        Long memberCount,
        String leaderImage,
        String leaderName
) {

    public static CrewProfileResponse of(Crew crew, Member leader) {

        return new CrewProfileResponse(
                crew.getImage(),
                crew.getName(),
                crew.getDescription(),
                crew.getMemberCount(),
                leader.getProfileImage(),
                leader.getNickname()
        );
    }
}
