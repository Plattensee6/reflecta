package hu.test.reflecta.meeting.data.spec;
import hu.test.reflecta.meeting.data.model.Meeting;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Utility class for building {@link Specification} filters for {@link Meeting} entities.
 */
public class MeetingSpecificationBuilder {
    private MeetingSpecificationBuilder() {
    }

    /**
     * Builds a {@link Specification} combining multiple optional filtering criteria for meetings.
     *
     * @param participantUserId the ID of the participant to filter by (can be {@code null})
     * @param title             a substring to filter by meeting title (can be {@code null})
     * @param startDate         the minimum start date/time of the meeting (can be {@code null})
     * @param endDate           the maximum end date/time of the meeting (can be {@code null})
     * @param finalized         whether the meeting is finalized (can be {@code null})
     * @return a {@link Specification} representing the combined filters
     */
    public static Specification<Meeting> build(
            final Long participantUserId,
            final String title,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final Boolean finalized
    ) {
        return Specification
                .where(MeetingSpecification.participant(participantUserId))
                .and(MeetingSpecification.titleContains(title))
                .and(MeetingSpecification.startsAfter(startDate))
                .and(MeetingSpecification.endsBefore(endDate))
                .and(MeetingSpecification.isFinalized(finalized));
    }
}
