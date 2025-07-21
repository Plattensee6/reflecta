package hu.test.reflecta.meeting.data.spec;
import hu.test.reflecta.meeting.data.model.Meeting;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MeetingSpecification {
    private MeetingSpecification() {
    }
    /**
     * Filter meetings, where the current user is a participant.(manager vagy employee).
     *
     * @param userId currentUserID
     * @return Specification<Meeting>
     */
    public static Specification<Meeting> participant(final Long userId) {
        if (userId == null) {
            return null;
        }
        return (root, query, builder) -> {
            final var manager = root.join("manager");
            final var employee = root.join("employee");
            return builder.or(
                    builder.equal(manager.get("id"), userId),
                    builder.equal(employee.get("id"), userId)
            );
        };
    }
    /**
     * Filter by title (LIKE, case insensitive).
     *
     * @param title
     * @return Specification<Meeting>
     */
    public static Specification<Meeting> titleContains(final String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        return (root, query, builder) ->
                builder.like(
                        builder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                );
    }

    public static Specification<Meeting> equalsByMeetingId(final Long id) {
        if (id == null) {
            return null;
        }
        return ((root, query, builder) -> builder.equal(root.get("id"), id));
    }

    /**
     * Filter by start date and time (start >= param).
     *
     * @param dateTime min start time
     * @return Specification<Meeting>
     */
    public static Specification<Meeting> startsAfter(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("startDateTime"),
                        dateTime
                );
    }

    /**
     * Filter by end date and time (end <= param).
     *
     * @param dateTime
     * @return Specification<Meeting>
     */
    public static Specification<Meeting> endsBefore(final LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("endDateTime"),
                        dateTime
                );
    }

    /**
     * Filter by whether the meeting is finalized.
     *
     * @param finalized true/false véglegesített-e
     * @return Specification<Meeting>
     */
    public static Specification<Meeting> isFinalized(final Boolean finalized) {
        if (finalized == null) {
            return null;
        }
        return (root, query, builder) ->
                builder.equal(root.get("isFinalized"), finalized);
    }
}
