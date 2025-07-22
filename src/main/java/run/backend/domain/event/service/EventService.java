package run.backend.domain.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.repository.JoinCrewRepository;
import run.backend.domain.event.dto.request.EventInfoRequest;
import run.backend.domain.event.dto.response.EventCreationValidationDto;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.event.entity.PeriodicEvent;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.exception.EventException.InvalidEventCreationRequest;
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
}
