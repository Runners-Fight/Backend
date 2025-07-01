package run.backend.domain.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import run.backend.domain.auth.dto.request.SignupRequest;
import run.backend.domain.auth.dto.response.SignupResponse;
import run.backend.domain.auth.dto.response.TokenResponse;
import run.backend.domain.auth.entity.RefreshToken;
import run.backend.domain.auth.repository.RefreshTokenRepository;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.OAuthType;
import run.backend.domain.member.enums.Role;
import run.backend.domain.member.repository.MemberRepository;
import run.backend.global.exception.ApplicationException;
import run.backend.global.exception.ExceptionCode;
import run.backend.global.oauth2.OAuth2UserInfo;
import run.backend.global.oauth2.OAuth2UserInfoFactory;
import run.backend.global.security.CustomUserDetails;
import run.backend.global.util.JwtTokenProvider;
import run.backend.domain.file.service.FileService;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileService fileService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SignupResponse socialLogin(String providerName, String authorizationCode) {
        ClientRegistration provider = clientRegistrationRepository.findByRegistrationId(providerName.toLowerCase());
        String accessToken = getAccessToken(authorizationCode, provider);
        Map<String, Object> userAttributes = getUserAttributes(accessToken, provider);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerName, userAttributes);

        return memberRepository.findByOauthId(userInfo.getProviderId())
            .map(member -> {
                Authentication authentication = createAuthentication(member, userAttributes);
                TokenResponse tokens = jwtTokenProvider.generateToken(authentication);
                saveRefreshToken(tokens.refreshToken(), member);
                return SignupResponse.forExistingUser(tokens);
            })
            .orElseGet(() -> {
                String signupToken = jwtTokenProvider.generateSignupToken(
                    userInfo.getProviderId(),
                    userInfo.getProvider(),
                    userInfo.getEmail(),
                    userInfo.getName()
                );
                return SignupResponse.forNewUser(signupToken, userInfo.getEmail(), userInfo.getName(), userInfo.getProvider());
            });
    }

    public TokenResponse completeSignup(SignupRequest signupRequest, MultipartFile profileImage) {
        if (!jwtTokenProvider.validateToken(signupRequest.signupToken())) {
            throw new ApplicationException(ExceptionCode.INVALID_SIGNUP_TOKEN);
        }

        Claims claims = jwtTokenProvider.parseClaims(signupRequest.signupToken());

        String oauthId = claims.getSubject();
        String providerName = claims.get("provider", String.class);
        String name = claims.get("name", String.class);

        memberRepository.findByOauthId(oauthId).ifPresent(m -> {
            throw new ApplicationException(ExceptionCode.USER_ALREADY_EXISTS);
        });

        String profileImageName = fileService.saveProfileImage(profileImage);

        Member newMember = Member.builder()
            .username(name)
            .nickname(signupRequest.nickname())
            .gender(signupRequest.gender())
            .age(signupRequest.age())
            .oauthId(oauthId)
            .oauthType(OAuthType.valueOf(providerName.toUpperCase()))
            .role(Role.USER)
            .profileImage(profileImageName)
            .build();
        memberRepository.save(newMember);

        Authentication authentication = createAuthentication(newMember, null);
        TokenResponse tokens = jwtTokenProvider.generateToken(authentication);
        saveRefreshToken(tokens.refreshToken(), newMember);

        return tokens;
    }

    private Authentication createAuthentication(Member member, Map<String, Object> attributes) {
        CustomUserDetails userDetails = new CustomUserDetails(member, attributes);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String getAccessToken(String authorizationCode, ClientRegistration provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", provider.getClientId());
        params.add("client_secret", provider.getClientSecret());
        params.add("redirect_uri", provider.getRedirectUri());
        params.add("grant_type", provider.getAuthorizationGrantType().getValue());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        String tokenUri = provider.getProviderDetails().getTokenUri();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            return (String) responseBody.get("access_token");
        } catch (Exception e) { throw new ApplicationException(ExceptionCode.OAUTH_REQUEST_FAILED); }
    }

    private Map<String, Object> getUserAttributes(String accessToken, ClientRegistration provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);
        String userInfoUri = provider.getProviderDetails().getUserInfoEndpoint().getUri();
        try {
            ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, String.class);
            return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        } catch (Exception e) { throw new ApplicationException(ExceptionCode.OAUTH_REQUEST_FAILED); }
    }

    public TokenResponse refreshTokens(String authorizationCode) {
        String refreshToken = extractTokenFromHeader(authorizationCode);

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApplicationException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new ApplicationException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshTokenEntity.isExpired()) {
            throw new ApplicationException(ExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        Member member = refreshTokenEntity.getMember();

        Authentication authentication = createAuthentication(member, null);
        TokenResponse newTokens = jwtTokenProvider.generateToken(authentication);

        saveRefreshToken(newTokens.refreshToken(), member);

        return newTokens;
    }

    private void saveRefreshToken(String refreshToken, Member member) {
        refreshTokenRepository.deleteByMember(member);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
            .token(refreshToken)
            .member(member)
            .expiresAt(jwtTokenProvider.getRefreshTokenExpiresAt(refreshToken))
            .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApplicationException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }
        return authorizationHeader.substring(7);
    }
}