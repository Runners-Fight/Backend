package run.backend.domain.auth.dto.response;

public record SignupResponse(boolean isNewUser, String signupToken, String email, String name,
                     String provider, TokenResponse tokens) {

    public static SignupResponse forExistingUser(TokenResponse tokens) {
        //기존 회원은 토큰만 리턴
        return new SignupResponse(false, null, null, null, null, tokens);
    }

    public static SignupResponse forNewUser(String signupToken, String email, String name,
        String provider) {
        //신규 회원은 회원가입을 위한 정보와 임시 토큰 리턴
        return new SignupResponse(true, signupToken, email, name, provider, null);
    }
}
