package run.backend.global.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google": return new GoogleUserInfo(attributes);
            default: throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}
