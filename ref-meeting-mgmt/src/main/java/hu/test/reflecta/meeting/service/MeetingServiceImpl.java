package hu.test.reflecta.meeting.service;

import hu.test.reflecta.auth.check.RequireParticipation;
import hu.test.reflecta.meeting.config.MeetingErrorMessages;
import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import hu.test.reflecta.meeting.data.mapper.MeetingMapper;
import hu.test.reflecta.meeting.data.model.Meeting;
import hu.test.reflecta.meeting.data.repository.MeetingRepository;
import hu.test.reflecta.meeting.data.spec.MeetingSpecificationBuilder;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.data.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingErrorMessages errorMessages;

    @Override
    @RequireParticipation(allowAdmin = true)
    @Transactional(readOnly = true)
    public MeetingResponse getById(Long id) {
        var entity = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        return MeetingMapper.toDto(entity);
    }

    @Override
    public MeetingResponse create(MeetingRequest request, Long currAppUserId) {
        var meeting = MeetingMapper.toEntity(request);
        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        meeting.setManager(manager);
        meeting.setEmployee(employee);
        var saved = meetingRepository.save(meeting);
        return MeetingMapper.toDto(saved);
    }

    @Override
    public MeetingResponse update(Long id, MeetingRequest request) {
        var entity = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        if (entity.getIsFinalized()) {
            throw new IllegalStateException(errorMessages.getUpdateFinalized());
        }
        entity.update(request);
        var updated = meetingRepository.save(entity);
        return MeetingMapper.toDto(updated);
    }

    @Override
    @RequireParticipation(allowAdmin = true)
    public void delete(Long id) {
        var entity = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        if (entity.getIsFinalized()) {
            throw new IllegalStateException(errorMessages.getDeleteFinalized());
        }
        meetingRepository.deleteById(id);
    }

    @Override
    @RequireParticipation
    public void finalizeMeeting(Long id) {
        var meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        if (meeting.getIsFinalized()) {
            throw new IllegalStateException(errorMessages.getFinalizedAlreadyExists());
        }
        final User employee = meeting.getEmployee(), manager = meeting.getManager();
        if (employee == null || manager == null) {
            throw new EntityNotFoundException(errorMessages.getUserNotFound());
        }
        boolean hasOverlap = meetingRepository.hasOverlappingFinalizedMeetings(
                meeting.getId(),
                meeting.getStartDateTime(),
                meeting.getEndDateTime(),
                meeting.getEmployee().getId(),
                meeting.getManager().getId()
        );
        if (hasOverlap) {
            throw new IllegalStateException(errorMessages.getFinalizedAlreadyExists());
        }
        meeting.finalizeMeeting();
        meetingRepository.save(meeting);
    }

    @Override
    @Transactional(readOnly = true)
    @RequireParticipation(allowAdmin = true)
    public Page<MeetingResponse> searchMeetings(Long currentUserId,
                                                String title,
                                                LocalDateTime start,
                                                LocalDateTime end,
                                                Boolean finalized,
                                                Long managerId,
                                                Long employeeId,
                                                Pageable pageable) {
        var spec = MeetingSpecificationBuilder.build(
                currentUserId, title, start, end, finalized
        );
        Page<Meeting> page = meetingRepository.findAll(spec, pageable);
        if (CollectionUtils.isEmpty(page.toList())) {
            throw new EntityNotFoundException(errorMessages.getMeetingNotFound());
        }
        return page.map(MeetingMapper::toDto);
    }
}
