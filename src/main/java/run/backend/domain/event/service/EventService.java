package run.backend.domain.event.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.dto.response.EventCreationValidationDto;
import run.backend.domain.event.dto.response.EventDetailResponse;
import run.backend.domain.event.dto.response.ParticipantDto;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.entity.PeriodicEvent;
import run.backend.domain.event.enums.EventStatus;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.exception.EventException.AlreadyJoinedEvent;
import run.backend.domain.event.exception.EventException.EventNotFound;
import run.backend.domain.event.exception.EventException.InvalidEventCreationRequest;
import run.backend.domain.event.exception.EventException.JoinEventNotFound;
import run.backend.domain.event.mapper.EventMapper;
import run.backend.domain.event.repository.EventRepository;
import run.backend.domain.event.repository.JoinEventRepository;
import run.backend.domain.event.repository.PeriodicEventRepository;
import run.backend.domain.member.entity.Member;
import run.backend.global.annotation.global.Logging;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final PeriodicEventRepository periodicEventRepository;
    private final JoinCrewRepository joinCrewRepository;
    private final JoinEventRepository joinEventRepository;
    private final EventMapper eventMapper;

    @Transactional
    @Logging
    public void createEvent(EventInfoRequest eventInfoRequest, Member member) {
        EventCreationValidationDto validation = joinCrewRepository
            .validateEventCreation(
                member.getId(),
                eventInfoRequest.runningCaptainId(),
                JoinStatus.APPROVED
            )
            .orElseThrow(InvalidEventCreationRequest::new);

        Crew crew = validation.crew();
        Member runningCaptain = validation.runningCaptain();

        if (eventInfoRequest.repeatCycle() != RepeatCycle.NONE) {
            createPeriodicEvent(eventInfoRequest, crew, runningCaptain);
        }
        createSingleEvent(eventInfoRequest, crew, runningCaptain);
    }

    @Transactional
    @Logging
    public void updateEvent(Long eventId, EventInfoRequest eventUpdateRequest) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(EventNotFound::new);

        Member newRunningCaptain = validateNewRunningCaptain(eventUpdateRequest, event);

        if (newRunningCaptain != null && !event.getMember().getId()
            .equals(newRunningCaptain.getId())) {
            updateRunningCaptain(event, newRunningCaptain);
        }

        handlePeriodicEventUpdate(event, eventUpdateRequest, newRunningCaptain);

        event.updateEvent(
            eventUpdateRequest.title(),
            eventUpdateRequest.baseDate(),
            eventUpdateRequest.startTime(),
            eventUpdateRequest.endTime(),
            eventUpdateRequest.place(),
            newRunningCaptain
        );
    }

    private void createPeriodicEvent(EventInfoRequest request, Crew crew, Member runningCaptain) {
        PeriodicEvent periodicEvent = eventMapper.toPeriodicEvent(request, crew, runningCaptain);
        periodicEventRepository.save(periodicEvent);
    }

    private void createSingleEvent(EventInfoRequest request, Crew crew, Member runningCaptain) {
        Event event = eventMapper.toEvent(request, crew, runningCaptain);
        Event savedEvent = eventRepository.save(event);

        JoinEvent joinEvent = eventMapper.toJoinEvent(savedEvent, runningCaptain);
        joinEventRepository.save(joinEvent);
    }

    private Member validateNewRunningCaptain(EventInfoRequest request, Event event) {
        if (request.runningCaptainId() == null) {
            return null;
        }

        return joinCrewRepository
            .findCrewMemberById(
                request.runningCaptainId(),
                event.getCrew().getId(),
                JoinStatus.APPROVED
            )
            .orElseThrow(InvalidEventCreationRequest::new);
    }

    private void updateRunningCaptain(Event event, Member newRunningCaptain) {
        joinEventRepository.findByEventAndMember(event, event.getMember())
            .ifPresent(joinEventRepository::delete);

        JoinEvent newJoinEvent = eventMapper.toJoinEvent(event, newRunningCaptain);
        joinEventRepository.save(newJoinEvent);
    }

    private void handlePeriodicEventUpdate(Event event, EventInfoRequest request,
        Member newRunningCaptain) {
        Optional<PeriodicEvent> existingPeriodicEvent = periodicEventRepository
            .findByCrewAndTitleAndTime(
                event.getCrew(),
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime()
            );

        RepeatCycle requestedRepeatCycle = request.repeatCycle();

        if (requestedRepeatCycle == null || requestedRepeatCycle == RepeatCycle.NONE) {
            existingPeriodicEvent.ifPresent(periodicEventRepository::delete);
        } else {
            if (existingPeriodicEvent.isPresent()) {
                PeriodicEvent periodicEvent = existingPeriodicEvent.get();
                periodicEvent.updatePeriodicEvent(
                    request.title(),
                    request.baseDate(),
                    requestedRepeatCycle,
                    request.repeatDays(),
                    request.startTime(),
                    request.endTime(),
                    request.place(),
                    newRunningCaptain
                );
            } else {
                EventInfoRequest eventInfoRequest = eventMapper.toEventInfoRequest(request, event);
                PeriodicEvent newPeriodicEvent = eventMapper.toPeriodicEvent(
                    eventInfoRequest,
                    event.getCrew(),
                    newRunningCaptain != null ? newRunningCaptain : event.getMember()
                );
                periodicEventRepository.save(newPeriodicEvent);
            }
        }
    }

    @Logging
    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(EventNotFound::new);

        List<JoinEvent> participants = getParticipants(event, event.getStatus());

        List<ParticipantDto> participantDtos = eventMapper.toParticipantDtoList(participants);

        return eventMapper.toEventDetailResponse(event, event.getStatus(), participantDtos);
    }

    private List<JoinEvent> getParticipants(Event event, EventStatus status) {
        return status == EventStatus.COMPLETED
            ? joinEventRepository.findActualParticipantsByEvent(event)
            : joinEventRepository.findByEvent(event);
    }

    @Transactional
    @Logging
    public void joinEvent(Long eventId, Member member) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(EventNotFound::new);

        if (joinEventRepository.existsByEventAndMember(event, member)) {
            throw new AlreadyJoinedEvent();
        }

        JoinEvent joinEvent = eventMapper.toJoinEvent(event, member);
        joinEventRepository.save(joinEvent);

        event.incrementExpectedParticipants();
    }

    @Transactional
    @Logging
    public void cancelJoinEvent(Long eventId, Member member) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(EventNotFound::new);

        JoinEvent joinEvent = joinEventRepository.findByEventAndMember(event, member)
            .orElseThrow(JoinEventNotFound::new);

        joinEventRepository.delete(joinEvent);

        event.decrementExpectedParticipants();
    }
}
