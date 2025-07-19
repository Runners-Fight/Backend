package run.backend.domain.event.dto.response;

import run.backend.domain.crew.entity.Crew;
import run.backend.domain.member.entity.Member;

public record EventCreationValidationDto(Crew crew, Member runningCaptain) {

}
