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
import run.backend.domain.event.entity.PeriodicEvent;
import run.backend.domain.event.enums.RepeatCycle;
import run.backend.domain.event.exception.EventException.InvalidEventCreationRequest;
import run.backend.domain.event.repository.EventRepository;
import run.backend.domain.event.repository.PeriodicEventRepository;
import run.backend.domain.member.entity.Member;
import run.backend.domain.member.enums.Role;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PeriodicEventRepository periodicEventRepository;
    private final JoinCrewRepository joinCrewRepository;

    @Override
    @Transactional
    public void createEvent(EventInfoRequest eventInfoRequest, Member member) {
        EventCreationValidationDto validation = joinCrewRepository
            .validateEventCreation(
                member.getId(), 
                eventInfoRequest.runningCaptainId(), 
                JoinStatus.APPROVED,
                Role.getManagementRoles()
            )
            .orElseThrow(InvalidEventCreationRequest::new);
        
        Crew crew = validation.crew();
        Member runningCaptain = validation.runningCaptain();
        
        if (eventInfoRequest.repeatCycle() == RepeatCycle.NONE) {
            createSingleEvent(eventInfoRequest, crew, runningCaptain);
        } else {
            createPeriodicAndSingleEvent(eventInfoRequest, crew, runningCaptain);
        }
    }

    private void createSingleEvent(EventInfoRequest request, Crew crew, Member runningCaptain) {
        Event event = Event.builder()
                .title(request.title())
                .date(request.baseDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .place(request.place())
                .crew(crew)
                .member(runningCaptain)
                .build();
        
        eventRepository.save(event);
    }

    private void createPeriodicAndSingleEvent(EventInfoRequest request, Crew crew, Member runningCaptain) {
        PeriodicEvent periodicEvent = PeriodicEvent.builder()
                .title(request.title())
                .baseDate(request.baseDate())
                .repeatCycle(request.repeatCycle())
                .repeatDays(request.repeatDays())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .place(request.place())
                .crew(crew)
                .member(runningCaptain)
                .build();
        
        Event event = Event.builder()
                .title(request.title())
                .date(request.baseDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .place(request.place())
                .crew(crew)
                .member(runningCaptain)
                .build();
        
        periodicEventRepository.save(periodicEvent);
        eventRepository.save(event);
    }
}
