package hu.test.reflecta.auth.check;

import hu.test.reflecta.auth.exception.AuthErrorMessages;
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

/**
 * Aspect that enforces participation-based access control for methods annotated with
 * {@link hu.test.reflecta.auth.check.RequireParticipation}.
 * <p>
 * This aspect checks whether the current authenticated user is a participant (or optionally an admin)
 * of the target resource before allowing method execution.
 * </p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ParticipantAspect {
    private final AuthService authService;
    private final AuthErrorMessages authErrorMessages;

    /**
     * Around advice that checks if the current user is a participant or has access
     * to the annotated method's target.
     *
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution if access is allowed
     * @throws Throwable if the target method throws an exception or access is denied
     */
    @Around("@annotation(hu.test.reflecta.auth.check.RequireParticipation)")
    public Object checkParticipation(final ProceedingJoinPoint joinPoint) throws Throwable {
        final Long currentUserId = authService.getCurrentUserId();
        if (currentUserId == null) {
            throw new InsufficientAuthenticationException(authErrorMessages.getUserNotAuthenticated());
        }
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final RequireParticipation annotation = method.getAnnotation(RequireParticipation.class);
        boolean allowAdmin = annotation.allowAdmin();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Participant meeting) {
                if (!authService.isEligible(meeting, currentUserId, allowAdmin)) {
                    throw new AccessDeniedException(authErrorMessages.getAccessDenied());
                }
                break;
            }
        }
        return joinPoint.proceed();
    }
}
