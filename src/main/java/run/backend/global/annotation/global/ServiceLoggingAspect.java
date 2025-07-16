package run.backend.global.annotation.global;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("@annotation(run.backend.global.annotation.global.ServiceLog)")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        ServiceLog serviceLog = signature.getMethod().getAnnotation(ServiceLog.class);
        String description = serviceLog.description();

        log.info("--- [ServiceLog] {} - {}() 시작 ---", className, methodName);
        if (!description.isEmpty()) {
            log.info("--- [ServiceLog] 설명: {} ---", description);
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("--- [ServiceLog] {} - {}() 종료 (소요 시간: {} ms) ---", className, methodName, stopWatch.getTotalTimeMillis());
        }
        return result;
    }
}
