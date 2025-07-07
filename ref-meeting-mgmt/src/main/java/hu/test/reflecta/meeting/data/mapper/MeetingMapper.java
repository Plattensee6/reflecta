package hu.test.reflecta.meeting.data.mapper;

import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import hu.test.reflecta.meeting.data.model.Meeting;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

public class MeetingMapper {
    public static MeetingResponse toDto(final Meeting meeting) {
        return MeetingResponse.builder()
                .meetingId(meeting.getId())
                .employeeId(meeting.getEmployee() != null? meeting.getEmployee().getId() : null)
                .managerId(meeting.getEmployee() != null? meeting.getEmployee().getId() : null)
                .title(meeting.getTitle())
                .description(meeting.getDescription())
                .startTime(meeting.getStartDateTime())
                .endTime(meeting.getEndDateTime())
                .isFinalized(meeting.getIsFinalized())
                .build();
    }

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
