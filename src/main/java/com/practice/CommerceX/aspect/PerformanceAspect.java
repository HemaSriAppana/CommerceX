package com.practice.CommerceX.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log =
            LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("execution(* com.practice.CommerceX.service..*(..))")
    public Object measureExecutionTime(
            ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        log.info(
                "Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                end - start
        );

        return result;
    }
}