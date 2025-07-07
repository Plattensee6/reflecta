package hu.test.reflecta.meeting.service;

import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    MeetingResponse create(MeetingRequest request, Long currAppUserId);

    MeetingResponse getById(Long id);

    MeetingResponse update(Long id, MeetingRequest request);

    void delete(Long id);

    void finalizeMeeting(Long id);

    Page<MeetingResponse> searchMeetings(Long currentUserId,
                                         String title,
                                         LocalDateTime start,
                                         LocalDateTime end,
                                         Boolean finalized,
                                         Long managerId,
                                         Long employeeId,
                                         Pageable pageable);
}
