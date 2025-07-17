package run.backend.global.annotation.global;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("@annotation(run.backend.global.annotation.global.Logging)")
    public Object logServiceMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Logging serviceLog = signature.getMethod().getAnnotation(Logging.class);
        String description = serviceLog.description();

        Object[] args = joinPoint.getArgs();
        String parameterString = "";
        if (args != null && args.length > 0) {
            parameterString = " (파라미터: " + Arrays.toString(args) + ")";
        }

        log.info("--- [Logging] {} - {}(){}{} 시작 ---", className, methodName, parameterString, (description.isEmpty() ? "" : " - " + description));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info("--- [Logging] {} - {}() 종료 (소요 시간: {} ms) ---", className, methodName, stopWatch.getTotalTimeMillis());
        }
        return result;
    }
}