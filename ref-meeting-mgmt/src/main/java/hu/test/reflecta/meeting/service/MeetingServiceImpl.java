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

/**
 * Service implementation for managing {@link Meeting} operations.
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingErrorMessages errorMessages;

    /**
     * Retrieves a meeting by its ID.
     *
     * @param id the ID of the meeting
     * @return the {@link MeetingResponse} DTO
     * @throws EntityNotFoundException if the meeting does not exist
     */
    @Override
    @RequireParticipation(allowAdmin = true)
    @Transactional(readOnly = true)
    public MeetingResponse getById(final Long id) {
        final var entity = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        return MeetingMapper.toDto(entity);
    }

    /**
     * Creates a new meeting.
     *
     * @param request       the request data for the meeting
     * @param currAppUserId the current authenticated user's ID
     * @return the created {@link MeetingResponse}
     * @throws EntityNotFoundException if the specified employee or manager does not exist
     */
    @Override
    public MeetingResponse create(final MeetingRequest request, final Long currAppUserId) {
        final var meeting = MeetingMapper.toEntity(request);
        final User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        final User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        meeting.setManager(manager);
        meeting.setEmployee(employee);
        final var saved = meetingRepository.save(meeting);
        return MeetingMapper.toDto(saved);
    }

    /**
     * Updates an existing meeting.
     *
     * @param id      the ID of the meeting to update
     * @param request the updated meeting data
     * @return the updated {@link MeetingResponse}
     * @throws EntityNotFoundException if the meeting does not exist
     * @throws IllegalStateException   if the meeting is already finalized
     */
    @Override
    public MeetingResponse update(final Long id, final MeetingRequest request) {
        final var entity = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        if (entity.getIsFinalized()) {
            throw new IllegalStateException(errorMessages.getUpdateFinalized());
        }
        entity.update(request);
        final var updated = meetingRepository.save(entity);
        return MeetingMapper.toDto(updated);
    }

    /**
     * Deletes a meeting by ID.
     *
     * @param id the ID of the meeting to delete
     * @throws EntityNotFoundException if the meeting does not exist
     * @throws IllegalStateException   if the meeting is already finalized
     */
    @Override
    @RequireParticipation(allowAdmin = true)
    public void delete(final Long id) {
        final var entity = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        if (entity.getIsFinalized()) {
            throw new IllegalStateException(errorMessages.getDeleteFinalized());
        }
        meetingRepository.deleteById(id);
    }

    /**
     * Marks a meeting as finalized, validating overlaps and constraints.
     *
     * @param id the ID of the meeting to finalize
     * @throws EntityNotFoundException if the meeting or its participants do not exist
     * @throws IllegalStateException   if the meeting is already finalized or overlaps with another finalized meeting
     */
    @Override
    @RequireParticipation
    public void finalizeMeeting(final Long id) {
        final var meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessages.getMeetingNotFound()));
        if (meeting.getIsFinalized()) {
            throw new IllegalStateException(errorMessages.getFinalizedAlreadyExists());
        }
        final User employee = meeting.getEmployee(), manager = meeting.getManager();
        if (employee == null || manager == null) {
            throw new EntityNotFoundException(errorMessages.getUserNotFound());
        }
        final boolean hasOverlap = meetingRepository.hasOverlappingFinalizedMeetings(
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

    /**
     * Searches for meetings matching the specified criteria.
     *
     * @param currentUserId the ID of the current user
     * @param title         the title substring to filter by
     * @param start         the start date/time filter
     * @param end           the end date/time filter
     * @param finalized     whether to filter finalized meetings
     * @param managerId     (currently unused)
     * @param employeeId    (currently unused)
     * @param pageable      pagination and sorting information
     * @return a page of {@link MeetingResponse} DTOs
     * @throws EntityNotFoundException if no meetings are found
     */
    @Override
    @Transactional(readOnly = true)
    @RequireParticipation(allowAdmin = true)
    public Page<MeetingResponse> searchMeetings(final Long currentUserId,
                                                final String title,
                                                final LocalDateTime start,
                                                final LocalDateTime end,
                                                final Boolean finalized,
                                                final Long managerId,
                                                final Long employeeId,
                                                final Pageable pageable) {
        final var spec = MeetingSpecificationBuilder.build(
                currentUserId, title, start, end, finalized
        );
        final Page<Meeting> page = meetingRepository.findAll(spec, pageable);
        if (CollectionUtils.isEmpty(page.toList())) {
            throw new EntityNotFoundException(errorMessages.getMeetingNotFound());
        }
        return page.map(MeetingMapper::toDto);
    }
}
