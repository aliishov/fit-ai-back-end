package org.raul.fit_ai.common.aop;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Around("execution(* org.raul..*Service.*(..))")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		String methodName = pjp.getSignature().getName();

		try {
			Object result = pjp.proceed();
			log.info("{} executed in {}ms", methodName, System.currentTimeMillis() - start);

			return result;
		} catch (Throwable throwable) {
			log.error(throwable.getMessage(), throwable);
			throw throwable;
		}
	}

}
