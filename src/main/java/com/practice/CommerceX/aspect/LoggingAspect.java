package com.practice.CommerceX.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.practice.CommerceX.service..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String methodName =
                joinPoint.getSignature().getName();

        log.info("Method started: {}", methodName);

        Object result = joinPoint.proceed();

        log.info("Method completed: {}", methodName);

        return result;
    }
}