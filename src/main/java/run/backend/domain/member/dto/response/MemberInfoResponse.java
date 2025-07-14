package run.backend.domain.member.dto.response;

public record MemberInfoResponse(
        String profileImageUrl,
        String nickName,
        String crewName
) {
}
