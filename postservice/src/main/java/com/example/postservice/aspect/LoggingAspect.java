package com.example.postservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.example.postservice.service..*) || within(com.example.postservice.controller..*)")
    public void applicationLayer() {}

    @Around("applicationLayer()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().getName();

        log.info("→ {}.{}() args={}", className, methodName, Arrays.toString(pjp.getArgs()));
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();
            log.info("← {}.{}() completed in {}ms", className, methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("✗ {}.{}() threw {} after {}ms: {}",
                    className, methodName, ex.getClass().getSimpleName(),
                    System.currentTimeMillis() - start, ex.getMessage());
            throw ex;
        }
    }
}
