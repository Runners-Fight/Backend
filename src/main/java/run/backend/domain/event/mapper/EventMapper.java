package run.backend.domain.event.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import run.backend.domain.crew.dto.response.EventProfileResponse;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.entity.PeriodicEvent;
import run.backend.domain.member.entity.Member;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventMapper {

    @Mapping(target = "date", source = "request.baseDate")
    @Mapping(target = "member", source = "runningCaptain")
    Event toEvent(EventInfoRequest request, Crew crew, Member runningCaptain);

    @Mapping(target = "member", source = "runningCaptain")
    PeriodicEvent toPeriodicEvent(EventInfoRequest request, Crew crew, Member runningCaptain);

    @Mapping(target = "member", source = "runningCaptain")
    @Mapping(target = "event", source = "event")
    JoinEvent toJoinEvent(Event event, Member runningCaptain);

    @Mapping(target = "eventId", source = "id")
    @Mapping(target = "participants", source = "expectedParticipants")
    EventProfileResponse toEventProfile(Event event);

    List<EventProfileResponse> toEventProfileList(List<Event> events);
}
