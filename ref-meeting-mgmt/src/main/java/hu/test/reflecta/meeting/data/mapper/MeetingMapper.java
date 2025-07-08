package hu.test.reflecta.meeting.data.mapper;

import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import hu.test.reflecta.meeting.data.model.Meeting;

/**
 * Utility class for mapping between {@link Meeting} entities and their DTO representations.
 */
public class MeetingMapper {

    /**
     * Converts a {@link Meeting} entity to a {@link MeetingResponse} DTO.
     *
     * @param meeting the meeting entity to convert
     * @return a {@link MeetingResponse} containing the mapped fields
     */
    public static MeetingResponse toDto(final Meeting meeting) {
        return MeetingResponse.builder()
                .meetingId(meeting.getId())
                .employeeId(meeting.getEmployee() != null ? meeting.getEmployee().getId() : null)
                .managerId(meeting.getEmployee() != null ? meeting.getEmployee().getId() : null)
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .startTime(meeting.getStartDateTime())
                .endTime(meeting.getEndDateTime())
                .isFinalized(meeting.getIsFinalized())
                .build();
    }

    /**
     * Converts a {@link MeetingRequest} DTO to a {@link Meeting} entity.
     *
     * @param request the request DTO containing meeting details
     * @return a {@link Meeting} entity populated with the request data
     */
    public static Meeting toEntity(final MeetingRequest request) {
        return Meeting.builder()
                .title(request.getTitle())
                .isFinalized(request.getIsFinalized())
                .description(request.getDescription())
                .startDateTime(request.getStartTime())
                .endDateTime(request.getEndTime())
                .build();
    }
}
