package run.backend.domain.event.enums;

public enum RunningStatus {

    NONE("일정 없음"),
    SCHEDULED("예정"),
    DONE("완료");

    private final String description;

    RunningStatus(String description) {
        this.description = description;
    }
}
