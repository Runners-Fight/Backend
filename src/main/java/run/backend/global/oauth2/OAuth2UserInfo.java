package run.backend.global.oauth2;

public interface OAuth2UserInfo {
    String getName();
    String getEmail();
    String getProvider();
    String getProviderId();
}
