package run.backend.domain.auth.dto.request;

import run.backend.domain.member.enums.Gender;

public record SignupRequest(String signupToken, String nickname, Gender gender, int age) {

}
