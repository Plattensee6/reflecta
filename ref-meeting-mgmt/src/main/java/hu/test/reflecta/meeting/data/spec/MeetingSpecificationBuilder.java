package hu.test.reflecta.meeting.data.spec;
import hu.test.reflecta.meeting.data.model.Meeting;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Utility class for building {@link Specification} filters for {@link Meeting} entities.
 */
public class MeetingSpecificationBuilder {
    private Long employeeId;
    private Long managerId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean finalized;
    private Long meetingId;


    public MeetingSpecificationBuilder withMeetingId(Long id) {
        this.meetingId = id;
        return this;
    }

    public MeetingSpecificationBuilder withEmployee(Long uid) {
        this.employeeId = uid;
        return this;
    }

    public MeetingSpecificationBuilder withManager(Long uid) {
        this.managerId = uid;
        return this;
    }

    public MeetingSpecificationBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public MeetingSpecificationBuilder withStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public MeetingSpecificationBuilder withEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public MeetingSpecificationBuilder withFinalized(Boolean finalized) {
        this.finalized = finalized;
        return this;
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
    public Specification<Meeting> build() {
        return Specification
                .where(MeetingSpecification.participant(employeeId))
                .and(MeetingSpecification.participant(managerId))
                .and(MeetingSpecification.titleContains(title))
                .and(MeetingSpecification.startsAfter(startDate))
                .and(MeetingSpecification.endsBefore(endDate))
                .and(MeetingSpecification.isFinalized(finalized))
                .and(MeetingSpecification.equalsByMeetingId(meetingId));
    }
}
