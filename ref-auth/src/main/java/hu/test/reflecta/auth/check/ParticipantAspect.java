package hu.test.reflecta.auth.check;

import hu.test.reflecta.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ParticipantAspect {
    private final AuthService authService;

    @Around("@annotation(hu.test.reflecta.auth.check.RequireParticipation)")
    public Object checkParticipation(ProceedingJoinPoint joinPoint) throws Throwable {
        Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new InsufficientAuthenticationException("User not authenticated.");
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireParticipation annotation = method.getAnnotation(RequireParticipation.class);
        boolean allowAdmin = annotation.allowAdmin();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Participant meeting) {
                if (!authService.isEligible(meeting, currentUserId, allowAdmin)) {
                    throw new AccessDeniedException("You are not authorized to access this resource.");
                }
                break;
            }
        }
        return joinPoint.proceed();
    }
}
