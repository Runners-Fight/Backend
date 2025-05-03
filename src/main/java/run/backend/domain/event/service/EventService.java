package run.backend.domain.event.service;


import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.dto.response.EventInfoResponse;

public interface EventService {

    void updateEvent(EventInfoRequest eventInfoRequest);

    void joinEvent(Long eventId, Long memberId);

    void deleteEvent(Long eventId);

    EventInfoResponse getEventDetails(Long eventId);
}
