package run.backend.domain.crew.enums;

import lombok.Getter;

@Getter
public enum JoinStatus {

    APPLIED("가입 요청"),
    APPROVED("가입 승인");

    private final String description;

    JoinStatus(String description) {
        this.description = description;
    }
}
