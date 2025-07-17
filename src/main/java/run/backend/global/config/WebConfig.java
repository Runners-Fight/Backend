package run.backend.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import run.backend.global.annotation.member.LoginArgumentResolver;
import run.backend.global.annotation.member.MemberCrewArgumentResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginArgumentResolver loginArgumentResolver;
    private final MemberCrewArgumentResolver memberCrewArgumentResolver;

    @Autowired
    public WebConfig(LoginArgumentResolver loginArgumentResolver, 
                     MemberCrewArgumentResolver memberCrewArgumentResolver) {
        this.loginArgumentResolver = loginArgumentResolver;
        this.memberCrewArgumentResolver = memberCrewArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver);
        resolvers.add(memberCrewArgumentResolver);
    }
}
