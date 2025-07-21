package run.backend.domain.event.mapper;

import org.springframework.stereotype.Component;
import run.backend.domain.crew.dto.response.EventProfileResponse;
import run.backend.domain.event.entity.Event;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public List<EventProfileResponse> toEventProfileList(List<Event> events) {
        return events.stream()
                .map(this::toEventProfile)
                .collect(Collectors.toList());
    }

    public EventProfileResponse toEventProfile(Event event) {
        return EventProfileResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .date(event.getDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .participants(event.getExpectedParticipants())
                .build();
    }
}
