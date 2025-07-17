package run.backend.global.annotation.member;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import run.backend.domain.crew.entity.Crew;
import run.backend.domain.crew.entity.JoinCrew;
import run.backend.domain.crew.enums.JoinStatus;
import run.backend.domain.crew.repository.JoinCrewRepository;

@Component
@RequiredArgsConstructor
public class MemberCrewArgumentResolver implements HandlerMethodArgumentResolver {

    private final JoinCrewRepository joinCrewRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasMemberCrewAnnotation = parameter.hasParameterAnnotation(MemberCrew.class);
        boolean isCrewType = parameter.getParameterType().equals(Crew.class);
        
        return hasMemberCrewAnnotation && isCrewType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Long memberId = getCurrentMemberId();
        
        JoinCrew joinCrew = joinCrewRepository.findByMemberIdAndJoinStatus(memberId, JoinStatus.APPROVED)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 속한 크루가 없습니다"));
        
        return joinCrew.getCrew();
    }

    private Long getCurrentMemberId() {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        // return principal.getMember().getId();
        
        return 1L;
    }
}
