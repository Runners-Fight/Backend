package run.backend.domain.event.enums;

import lombok.Getter;

@Getter
public enum RepeatCycle {

    NONE("주기 없음"),
    WEEKLY("1주 마다");

    private final String description;

    RepeatCycle(String description) {
        this.description = description;
    }
}
