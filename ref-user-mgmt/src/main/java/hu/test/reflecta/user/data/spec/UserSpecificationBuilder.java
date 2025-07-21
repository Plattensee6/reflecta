package hu.test.reflecta.user.data.spec;

import hu.test.reflecta.user.data.model.Position;
import hu.test.reflecta.user.data.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Utility class for building {@link Specification} filters for {@link User} entities.
 */
public class UserSpecificationBuilder {
    private Long userId;
    private String name;
    private Position position;
    private LocalDateTime dateOfBirth;
    private String email;

    public UserSpecificationBuilder withUserId(final Long userId) {
        this.userId = userId;
        return this;
    }

    public UserSpecificationBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public UserSpecificationBuilder withPosition(final Position position) {
        this.position = position;
        return this;
    }

    public UserSpecificationBuilder withDateOfBirth(final LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public UserSpecificationBuilder withEmail(final String email) {
        this.email = email;
        return this;
    }

    /**
     * Builds a {@link Specification} combining multiple optional filtering criteria for {@link User} entities.
     * Each filter is only applied if its corresponding value is not {@code null}.
     *
     * @return a {@link Specification} representing the combined user filters
     *
     * The following filters are supported:
     * <ul>
     *     <li><b>userId</b> – filters users by their unique identifier</li>
     *     <li><b>name</b> – filters users whose name contains the given substring (case-insensitive)</li>
     *     <li><b>position</b> – filters users by their position</li>
     *     <li><b>dateOfBirth</b> – filters users by exact date of birth</li>
     *     <li><b>email</b> – filters users by exact email address</li>
     * </ul>
     */
    public Specification<User> build() {
        return Specification
                .where(UserSpecification.hasUserId(userId))
                .and(UserSpecification.hasDateOfBirth(dateOfBirth))
                .and(UserSpecification.hasEmail(email))
                .and(UserSpecification.hasPosition(position))
                .and(UserSpecification.nameContains(name));
    }
}
