package run.backend.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import run.backend.domain.event.entity.Event;
import run.backend.domain.event.entity.JoinEvent;
import run.backend.domain.member.entity.Member;

public interface JoinEventRepository extends JpaRepository<JoinEvent, Long> {
    
    void deleteByEventAndMember(Event event, Member member);
    
}
