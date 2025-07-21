package hu.test.reflecta.user.data.spec;

import hu.test.reflecta.user.data.model.Position;
import hu.test.reflecta.user.data.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Utility class providing static methods to build {@link Specification} filters
 * for querying {@link User} entities in a flexible and composable way.
 * <p>
 * These specifications can be combined using {@code Specification.and()} or {@code Specification.or()}
 * to build complex queries for filtering users based on multiple optional criteria.
 */
public class UserSpecification {

    /**
     * Creates a {@link Specification} to filter users by their ID.
     *
     * @param userId the ID of the user to match
     * @return a {@link Specification} that matches the given user ID
     */
    public static Specification<User> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("id"), userId);
    }

    /**
     * Creates a {@link Specification} to filter users whose name contains the given substring (case-insensitive).
     *
     * @param name the substring to search for in user names
     * @return a {@link Specification} that matches users with names containing the given substring
     */
    public static Specification<User> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     * Creates a {@link Specification} to filter users by their position.
     *
     * @param position the position to match
     * @return a {@link Specification} that matches the given position
     */
    public static Specification<User> hasPosition(Position position) {
        return (root, query, cb) -> cb.equal(root.get("position"), position);
    }

    /**
     * Creates a {@link Specification} to filter users by their exact date of birth.
     *
     * @param dateOfBirth the date of birth to match
     * @return a {@link Specification} that matches the given date of birth
     */
    public static Specification<User> hasDateOfBirth(LocalDateTime dateOfBirth) {
        return (root, query, cb) -> cb.equal(root.get("dateOfBirth"), dateOfBirth);
    }

    /**
     * Creates a {@link Specification} to filter users by their email address.
     *
     * @param email the email address to match
     * @return a {@link Specification} that matches the given email address
     */
    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }
}
