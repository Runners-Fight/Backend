package run.backend.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.lenient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import run.backend.domain.auth.dto.request.SignupRequest;
import run.backend.domain.auth.dto.response.SignupResponse;
import run.backend.domain.auth.dto.response.TokenResponse;
import run.backend.domain.auth.repository.RefreshTokenRepository;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Gender;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.member.enums.Role;
import run.backend.domain.member.repository.MemberRepository;
import run.backend.global.exception.ApplicationException;
import run.backend.global.exception.ExceptionCode;
import run.backend.global.oauth2.GoogleUserInfo;
import run.backend.global.oauth2.OAuth2UserInfo;
import run.backend.global.oauth2.OAuth2UserInfoFactory;
import run.backend.global.util.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private run.backend.domain.file.service.FileService fileService;

    @InjectMocks
    private AuthService authService;

    private ClientRegistration clientRegistration;
    private Member existingMember;
    private TokenResponse tokenResponse;
    private Map<String, Object> googleUserAttributes;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(authService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(authService, "objectMapper", objectMapper);

        lenient().when(jwtTokenProvider.getRefreshTokenExpiresAt(anyString()))
            .thenReturn(LocalDateTime.now().plusDays(14));

        clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v2/userinfo")
                .userNameAttributeName("id")
                .build();

        googleUserAttributes = new HashMap<>();
        googleUserAttributes.put("sub", "123456789");
        googleUserAttributes.put("email", "test@example.com");
        googleUserAttributes.put("name", "테스트 유저");

        existingMember = Member.builder()
                .username("테스트 유저")
                .nickname("기존 유저")
                .gender(Gender.MALE)
                .age(25)
                .oauthId("123456789")
                .oauthType(OAuthType.GOOGLE)
                .role(Role.USER)
                .build();

        tokenResponse = new TokenResponse("access-token", "refresh-token");
    }

    private void setupSuccessfulOAuthFlow(String accessToken) throws Exception {
        Map<String, Object> tokenResponseBody = new HashMap<>();
        tokenResponseBody.put("access_token", accessToken);
        ResponseEntity<String> tokenResponseEntity = ResponseEntity.ok("{\"access_token\":\"" + accessToken + "\"}");

        given(restTemplate.postForEntity(eq(clientRegistration.getProviderDetails().getTokenUri()), 
                any(HttpEntity.class), eq(String.class)))
                .willReturn(tokenResponseEntity);

        given(objectMapper.readValue(eq(tokenResponseEntity.getBody()), any(TypeReference.class)))
                .willReturn(tokenResponseBody);

        ResponseEntity<String> userInfoResponseEntity = ResponseEntity.ok(
            "{\"sub\":\"123456789\",\"email\":\"test@example.com\",\"name\":\"테스트 유저\"}"
        );
        given(restTemplate.exchange(eq(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()), 
                eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .willReturn(userInfoResponseEntity);

        given(objectMapper.readValue(eq(userInfoResponseEntity.getBody()), any(TypeReference.class)))
                .willReturn(googleUserAttributes);
    }

    private Claims createMockClaims(String subject, String provider, String email, String name) {
        Claims claims = Mockito.mock(Claims.class, Mockito.withSettings().lenient());
        given(claims.getSubject()).willReturn(subject);
        given(claims.get("provider", String.class)).willReturn(provider);
        given(claims.get("email", String.class)).willReturn(email);
        given(claims.get("name", String.class)).willReturn(name);
        return claims;
    }

    @Nested
    @DisplayName("socialLogin 메소드 테스트")
    class SocialLoginTest {

        @Test
        @DisplayName("기존 회원 로그인 성공")
        void socialLogin_ExistingUser_Success() throws Exception {
            // given
            String providerName = "google";
            String authorizationCode = "auth-code";
            String accessToken = "access-token";

            given(clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase()))
                    .willReturn(clientRegistration);

            setupSuccessfulOAuthFlow(accessToken);

            try (MockedStatic<OAuth2UserInfoFactory> mockedFactory = Mockito.mockStatic(OAuth2UserInfoFactory.class)) {
                OAuth2UserInfo mockUserInfo = new GoogleUserInfo(googleUserAttributes);
                mockedFactory.when(() -> OAuth2UserInfoFactory.getOAuth2UserInfo(providerName, googleUserAttributes))
                        .thenReturn(mockUserInfo);

                given(memberRepository.findByOauthId("123456789"))
                        .willReturn(Optional.of(existingMember));

                given(jwtTokenProvider.generateToken(any()))
                        .willReturn(tokenResponse);

                // when
                SignupResponse result = authService.socialLogin(providerName, authorizationCode);

                // then
                assertThat(result.isNewUser()).isFalse();
                assertThat(result.tokens()).isEqualTo(tokenResponse);
                assertThat(result.signupToken()).isNull();
                assertThat(result.email()).isNull();
                assertThat(result.name()).isNull();
                assertThat(result.provider()).isNull();

                verify(memberRepository).findByOauthId("123456789");
                verify(jwtTokenProvider).generateToken(any());
            }
        }

        @Test
        @DisplayName("신규 회원 가입 필요")
        void socialLogin_NewUser_RequiresSignup() throws Exception {
            // given
            String providerName = "google";
            String authorizationCode = "auth-code";
            String accessToken = "access-token";
            String signupToken = "signup-token";

            given(clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase()))
                    .willReturn(clientRegistration);

            setupSuccessfulOAuthFlow(accessToken);

            try (MockedStatic<OAuth2UserInfoFactory> mockedFactory = Mockito.mockStatic(OAuth2UserInfoFactory.class)) {
                OAuth2UserInfo mockUserInfo = new GoogleUserInfo(googleUserAttributes);
                mockedFactory.when(() -> OAuth2UserInfoFactory.getOAuth2UserInfo(providerName, googleUserAttributes))
                        .thenReturn(mockUserInfo);

                given(memberRepository.findByOauthId("123456789"))
                        .willReturn(Optional.empty());

                given(jwtTokenProvider.generateSignupToken("123456789", "google", "test@example.com", "테스트 유저"))
                        .willReturn(signupToken);

                // when
                SignupResponse result = authService.socialLogin(providerName, authorizationCode);

                // then
                assertThat(result.isNewUser()).isTrue();
                assertThat(result.signupToken()).isEqualTo(signupToken);
                assertThat(result.email()).isEqualTo("test@example.com");
                assertThat(result.name()).isEqualTo("테스트 유저");
                assertThat(result.provider()).isEqualTo("google");
                assertThat(result.tokens()).isNull();

                verify(memberRepository).findByOauthId("123456789");
                verify(jwtTokenProvider).generateSignupToken("123456789", "google", "test@example.com", "테스트 유저");
            }
        }

        @Test
        @DisplayName("OAuth 요청 실패 시 예외 발생")
        void socialLogin_OAuthRequestFailed_ThrowsException() {
            // given
            String providerName = "google";
            String authorizationCode = "auth-code";

            given(clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase()))
                    .willReturn(clientRegistration);

            given(restTemplate.postForEntity(eq(clientRegistration.getProviderDetails().getTokenUri()), 
                    any(HttpEntity.class), eq(String.class)))
                    .willThrow(new RuntimeException("Network error"));

            // when & then
            assertThatThrownBy(() -> authService.socialLogin(providerName, authorizationCode))
                    .isInstanceOf(ApplicationException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.OAUTH_REQUEST_FAILED);
        }
    }

    @Nested
    @DisplayName("completeSignup 메소드 테스트")
    class CompleteSignupTest {

        @Test
        @DisplayName("회원가입 완료 성공")
        void completeSignup_Success() {
            // given
            String signupToken = "valid-signup-token";
            SignupRequest signupRequest = new SignupRequest(
                    signupToken,
                    "테스트 닉네임",
                    Gender.MALE,
                    25
            );

            Claims claims = createMockClaims("123456789", "google", "test@example.com", "테스트 유저");

            given(jwtTokenProvider.validateToken(signupToken)).willReturn(true);
            given(jwtTokenProvider.parseClaims(signupToken)).willReturn(claims);
            given(memberRepository.findByOauthId("123456789")).willReturn(Optional.empty());
            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));
            given(jwtTokenProvider.generateToken(any())).willReturn(tokenResponse);

            // when
            TokenResponse result = authService.completeSignup(signupRequest, null);

            // then
            assertThat(result).isEqualTo(tokenResponse);

            verify(jwtTokenProvider).validateToken(signupToken);
            verify(jwtTokenProvider).parseClaims(signupToken);
            verify(memberRepository).findByOauthId("123456789");
            verify(memberRepository).save(argThat(member -> 
                    member.getNickname().equals("테스트 닉네임") &&
                    member.getGender().equals(Gender.MALE) &&
                    member.getAge() == 25 &&
                    member.getOauthId().equals("123456789") &&
                    member.getOauthType().equals(OAuthType.GOOGLE) &&
                    member.getRole().equals(Role.USER) &&
                    member.getUsername().equals("테스트 유저")
            ));
            verify(jwtTokenProvider).generateToken(any());
        }

        @Test
        @DisplayName("유효하지 않은 가입 토큰으로 예외 발생")
        void completeSignup_InvalidSignupToken_ThrowsException() {
            // given
            String invalidToken = "invalid-token";
            SignupRequest signupRequest = new SignupRequest(
                    invalidToken,
                    "테스트 닉네임",
                    Gender.MALE,
                    25
            );

            given(jwtTokenProvider.validateToken(invalidToken)).willReturn(false);

            assertThatThrownBy(() -> authService.completeSignup(signupRequest, null))
                    .isInstanceOf(ApplicationException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_SIGNUP_TOKEN);

            verify(jwtTokenProvider).validateToken(invalidToken);
            verify(jwtTokenProvider, never()).parseClaims(anyString());
            verify(memberRepository, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("이미 존재하는 사용자로 예외 발생")
        void completeSignup_UserAlreadyExists_ThrowsException() {
            // given
            String signupToken = "valid-signup-token";
            SignupRequest signupRequest = new SignupRequest(
                    signupToken,
                    "테스트 닉네임",
                    Gender.MALE,
                    25
            );

            Claims claims = createMockClaims("123456789", "google", "test@example.com", "테스트 유저");

            given(jwtTokenProvider.validateToken(signupToken)).willReturn(true);
            given(jwtTokenProvider.parseClaims(signupToken)).willReturn(claims);
            given(memberRepository.findByOauthId("123456789")).willReturn(Optional.of(existingMember));

            // when & then
            assertThatThrownBy(() -> authService.completeSignup(signupRequest, null))
                    .isInstanceOf(ApplicationException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.USER_ALREADY_EXISTS);

            verify(jwtTokenProvider).validateToken(signupToken);
            verify(jwtTokenProvider).parseClaims(signupToken);
            verify(memberRepository).findByOauthId("123456789");
            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("매개변수 검증 테스트")
    class ParameterValidationTest {

        @Test
        @DisplayName("null 인증 코드로 소셜 로그인 시도")
        void socialLogin_NullAuthorizationCode_ThrowsException() {
            // given
            String providerName = "google";
            String authorizationCode = null;

            given(clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase()))
                    .willReturn(clientRegistration);

            // when & then
            assertThatThrownBy(() -> authService.socialLogin(providerName, authorizationCode))
                    .isInstanceOf(ApplicationException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.OAUTH_REQUEST_FAILED);
        }

        @Test
        @DisplayName("null 토큰으로 회원가입 완료 시도")
        void completeSignup_NullToken_ThrowsException() {
            // given
            SignupRequest signupRequest = new SignupRequest(
                    null,
                    "테스트 닉네임",
                    Gender.MALE,
                    25
            );

            given(jwtTokenProvider.validateToken(null)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.completeSignup(signupRequest, null))
                    .isInstanceOf(ApplicationException.class)
                    .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.INVALID_SIGNUP_TOKEN);
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("신규 사용자 가입 플로우")
        void completeNewUserSignupFlow() throws Exception {
            // given
            String providerName = "google";
            String authorizationCode = "auth-code";
            String accessToken = "access-token";
            String signupToken = "signup-token";

            given(clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase()))
                    .willReturn(clientRegistration);

            setupSuccessfulOAuthFlow(accessToken);

            try (MockedStatic<OAuth2UserInfoFactory> mockedFactory = Mockito.mockStatic(OAuth2UserInfoFactory.class)) {
                OAuth2UserInfo mockUserInfo = new GoogleUserInfo(googleUserAttributes);
                mockedFactory.when(() -> OAuth2UserInfoFactory.getOAuth2UserInfo(providerName, googleUserAttributes))
                        .thenReturn(mockUserInfo);

                given(memberRepository.findByOauthId("123456789"))
                        .willReturn(Optional.empty());

                given(jwtTokenProvider.generateSignupToken("123456789", "google", "test@example.com", "테스트 유저"))
                        .willReturn(signupToken);

                // when
                SignupResponse socialLoginResult = authService.socialLogin(providerName, authorizationCode);

                // then
                assertThat(socialLoginResult.isNewUser()).isTrue();
                assertThat(socialLoginResult.signupToken()).isEqualTo(signupToken);

                SignupRequest signupRequest = new SignupRequest(
                        signupToken,
                        "CompletedUser",
                        Gender.MALE,
                        25
                );

                Claims claims = createMockClaims("123456789", "google", "test@example.com", "테스트 유저");

                given(jwtTokenProvider.validateToken(signupToken)).willReturn(true);
                given(jwtTokenProvider.parseClaims(signupToken)).willReturn(claims);
                given(memberRepository.findByOauthId("123456789")).willReturn(Optional.empty());
                given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));
                given(jwtTokenProvider.generateToken(any())).willReturn(tokenResponse);

                // when
                TokenResponse completeSignupResult = authService.completeSignup(signupRequest, null);

                // then
                assertThat(completeSignupResult).isEqualTo(tokenResponse);

                verify(memberRepository, times(2)).findByOauthId("123456789");
                verify(memberRepository).save(any(Member.class));
                verify(jwtTokenProvider).generateSignupToken(anyString(), anyString(), anyString(), anyString());
                verify(jwtTokenProvider).generateToken(any());
            }
        }

        @Test
        @DisplayName("기존 사용자 로그인 플로우")
        void existingUserLoginFlow() throws Exception {
            // given
            String providerName = "google";
            String authorizationCode = "auth-code";
            String accessToken = "access-token";

            given(clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase()))
                    .willReturn(clientRegistration);

            setupSuccessfulOAuthFlow(accessToken);

            try (MockedStatic<OAuth2UserInfoFactory> mockedFactory = Mockito.mockStatic(OAuth2UserInfoFactory.class)) {
                OAuth2UserInfo mockUserInfo = new GoogleUserInfo(googleUserAttributes);
                mockedFactory.when(() -> OAuth2UserInfoFactory.getOAuth2UserInfo(providerName, googleUserAttributes))
                        .thenReturn(mockUserInfo);

                given(memberRepository.findByOauthId("123456789"))
                        .willReturn(Optional.of(existingMember));

                given(jwtTokenProvider.generateToken(any()))
                        .willReturn(tokenResponse);

                // when
                SignupResponse result = authService.socialLogin(providerName, authorizationCode);

                // then
                assertThat(result.isNewUser()).isFalse();
                assertThat(result.tokens()).isEqualTo(tokenResponse);

                verify(jwtTokenProvider, never()).generateSignupToken(anyString(), anyString(), anyString(), anyString());
                verify(memberRepository, never()).save(any(Member.class));
                verify(jwtTokenProvider).generateToken(any());
            }
        }
    }
}
