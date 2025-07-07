package hu.test.reflecta.auth.check;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;


/**
 * Indicates that the annotated method requires ownership validation
 * before execution. <br><br>
 *
 * When a method is annotated with {@code @RequireOwnership},
 * the {@link hu.test.reflecta.common.security.OwnershipAspect} aspect intercepts
 * the call and verifies that the current authenticated user
 * has ownership rights over the entity provided as the first argument.
 * <br><br>
 *
 * Usage example:
 * <pre>{@code
 * @RequireOwnership
 * public void updateEntity(MyEntity entity) {
 *     // Business logic here
 * }
 * }</pre>
 * <br>
 * OR
 *  * <pre>{@code
 *  * @RequireOwnership(allowsAdmin=true)
 *  * public void updateEntity(MyEntity entity) {
 *  *     // Business logic here
 *  * }
 *  * }</pre>
 *  * <br>
 *
 * The entity must implement the {@link hu.test.reflecta.auth.check.Participant} interface,
 * providing the logic for checking ownership (e.g. comparing entity owner ID with
 * the current user ID).
 *
 * <p>If the ownership check fails, an {@code AccessDeniedException} is thrown,
 * preventing further execution.
 *
 * @see hu.test.reflecta.auth.check.ParticipantAspect
 * @see hu.test.reflecta.auth.check.Participant
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireParticipation {
    boolean allowAdmin() default false;
}
