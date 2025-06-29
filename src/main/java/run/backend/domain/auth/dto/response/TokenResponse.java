package run.backend.domain.auth.dto.response;

public record TokenResponse(String accessToken, String refreshToken) {

}
