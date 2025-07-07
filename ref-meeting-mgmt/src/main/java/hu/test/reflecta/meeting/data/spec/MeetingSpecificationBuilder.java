package hu.test.reflecta.meeting.data.spec;
import hu.test.reflecta.meeting.data.model.Meeting;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MeetingSpecificationBuilder {
    private MeetingSpecificationBuilder() {
    }

    public static Specification<Meeting> build(
            Long participantUserId,
            String title,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean finalized
    ) {
        return Specification
                .where(MeetingSpecification.participant(participantUserId))
                .and(MeetingSpecification.titleContains(title))
                .and(MeetingSpecification.startsAfter(startDate))
                .and(MeetingSpecification.endsBefore(endDate))
                .and(MeetingSpecification.isFinalized(finalized));
    }
}
