package run.backend.domain.auth.exception;

import run.backend.global.exception.CustomException;

public class AuthException extends CustomException {

    public AuthException(final AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }

    public static class InvalidSignupToken extends AuthException {
        public InvalidSignupToken() {
            super(AuthErrorCode.INVALID_SIGNUP_TOKEN);
        }
    }

    public static class UserAlreadyExists extends AuthException {
        public UserAlreadyExists() {
            super(AuthErrorCode.USER_ALREADY_EXISTS);
        }
    }

    public static class OauthRequestFailed extends AuthException {
        public OauthRequestFailed() {
            super(AuthErrorCode.OAUTH_REQUEST_FAILED);
        }
    }

    public static class TokenMissingAuthority extends AuthException {
        public TokenMissingAuthority() {
            super(AuthErrorCode.TOKEN_MISSING_AUTHORITY);
        }
    }

    public static class InvalidRefreshToken extends AuthException {
        public InvalidRefreshToken() {
            super(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public static class RefreshTokenNotFound extends AuthException {
        public RefreshTokenNotFound() {
            super(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public static class RefreshTokenExpired extends AuthException {
        public RefreshTokenExpired() {
            super(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }
    }
}
