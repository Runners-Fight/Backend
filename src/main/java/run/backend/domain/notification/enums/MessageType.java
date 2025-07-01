package run.backend.domain.notification.enums;

import lombok.Getter;

@Getter
public enum MessageType {

    BATTLE("대결"),
    CREW("크루");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }
}
