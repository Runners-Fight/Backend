package run.backend.domain.member.enums;

import lombok.Getter;

@Getter
public enum Role {
    NONE("앱유저"),
    MEMBER("크루원"),
    LEADER("크루장"),
    MANAGER("운영진");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
