package kz.iitu.springlab.aspect;

import kz.iitu.springlab.retry.RetryOnFailure;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)
public class RetryAspect {

    private static final Logger log =
            LoggerFactory.getLogger(RetryAspect.class);

    @Around("@annotation(retryOnFailure)")
    public Object retry(
            ProceedingJoinPoint pjp,
            RetryOnFailure retryOnFailure
    ) throws Throwable {

        int maxAttempts = retryOnFailure.attempts();
        Throwable lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {
                log.info(
                        "[RETRY] {} attempt {}/{}",
                        pjp.getSignature().toShortString(),
                        attempt,
                        maxAttempts
                );

                return pjp.proceed();

            } catch (Throwable ex) {

                lastException = ex;

                log.warn(
                        "[RETRY] {} failed on attempt {}/{}: {}",
                        pjp.getSignature().toShortString(),
                        attempt,
                        maxAttempts,
                        ex.getMessage()
                );
            }
        }

        throw lastException;
    }
}