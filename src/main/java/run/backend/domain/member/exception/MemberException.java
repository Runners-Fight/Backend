package run.backend.domain.member.exception;

import run.backend.global.exception.CustomException;

public class MemberException extends CustomException {

    public MemberException(final MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }

    public static class MemberNotJoinedCrew extends MemberException {
        public MemberNotJoinedCrew() {
            super(MemberErrorCode.MEMBER_NOT_JOINED_CREW);
        }
    }
}
