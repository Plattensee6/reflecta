package hu.test.reflecta.meeting.service;

import hu.test.reflecta.meeting.config.MeetingErrorMessages;
import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import hu.test.reflecta.meeting.data.model.Meeting;
import hu.test.reflecta.meeting.data.repository.MeetingRepository;
import hu.test.reflecta.user.data.model.User;
import hu.test.reflecta.user.data.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MeetingServiceTest {
    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private MeetingErrorMessages errorMessages;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private Meeting meeting;
    private MeetingRequest meetingRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("Test user")
                .build();

        meeting = Meeting.builder()
                .id(1L)
                .title("Sprint Planning")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .isFinalized(false)
                .employee(user)
                .manager(user)
                .build();

        meetingRequest = MeetingRequest.builder()
                .title("Spring Planning DTO")
                .description("Desc DTO")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .isFinalized(false)
                .build();

        when(errorMessages.getMeetingNotFound()).thenReturn("Meeting not found.");
        when(errorMessages.getUserNotFound()).thenReturn("User not found.");
        when(errorMessages.getFinalizedAlreadyExists()).thenReturn("Another finalized meeting already exists in the selected time range.");
        when(errorMessages.getUpdateFinalized()).thenReturn("Cannot update a finalized meeting");
        when(errorMessages.getDeleteFinalized()).thenReturn("Cannot delete a finalized meeting");
        when(errorMessages.getInvalidTimeRange()).thenReturn("Meeting start date must be before end date.");
        when(errorMessages.getUnauthorized()).thenReturn("You are not allowed to modify this meeting.");
    }

    @Test
    void shouldCreateMeeting() {
        // arrange
        MeetingRequest request = MeetingRequest.builder()
                .title("Sprint Planning")
                .startTime(LocalDateTime.of(2025, 7, 6, 11, 21))
                .endTime(LocalDateTime.of(2025, 7, 6, 12, 21))
                .managerId(1L)
                .employeeId(2L)
                .description("Sprint planning session")
                .build();

        Meeting savedMeeting = Meeting.builder()
                .id(42L)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDateTime(request.getStartTime())
                .endDateTime(request.getEndTime())
                .manager(User.builder().id(1L).build())
                .employee(User.builder().id(2L).build())
                .isFinalized(false)
                .build();

        when(meetingRepository.save(any(Meeting.class)))
                .thenReturn(savedMeeting);

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        // act
        MeetingResponse response = meetingService.create(request, 1L);

        // assert
        assertThat(response).isNotNull();
        assertThat(response.getMeetingId()).isEqualTo(42L);
        assertThat(response.getTitle()).isEqualTo("Sprint Planning");
        assertThat(response.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(response.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(response.getIsFinalized()).isFalse();

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"WRITE"})
    void shouldUpdateMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(meetingRepository.save(ArgumentMatchers.any(Meeting.class))).thenReturn(meeting);
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));
        MeetingResponse updated = meetingService.update(1L, meetingRequest);
        assertNotNull(updated);
        verify(meetingRepository).findById(1L);
        verify(meetingRepository).save(meeting);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READ"})
    void updateShouldThrowIfMeetingNotFound() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.update(1L, meetingRequest))
                .isInstanceOf(EntityNotFoundException.class);

        verify(meetingRepository).findById(1L);
        verify(meetingRepository, never()).save(ArgumentMatchers.any(Meeting.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"WRITE"})
    void shouldDeleteMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        meetingService.delete(1L);

        verify(meetingRepository).deleteById(meeting.getId());
    }

    @WithMockUser(username = "testuser", roles = {"WRITE"})
    @Test
    void deleteShouldThrowIfMeetingNotFound() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READ"})
    void shouldFindById() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        MeetingResponse found = meetingService.getById(1L);

        assertNotNull(found);
        assertEquals(found.getMeetingId(), 1L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READ"})
    void shouldThrowIfNotFound() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READ"})
    void shouldFindAllWithFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Meeting> page = new PageImpl<>(List.of(meeting));

        when(meetingRepository.findAll(ArgumentMatchers.any(Specification.class), eq(pageable))).thenReturn(page);
//
        Page<MeetingResponse> result = meetingService.searchMeetings(
                1000L,
                "title",
                null, null, null, null, null,
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getSize());
        verify(meetingRepository).findAll(ArgumentMatchers.any(Specification.class), eq(pageable));
    }

    @Test
    void shouldFinalizeMeeting() {
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
        when(meetingRepository.save(ArgumentMatchers.any(Meeting.class))).thenReturn(meeting);
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));
        meetingService.finalizeMeeting(1L);

        assertTrue(meeting.getIsFinalized());
        verify(meetingRepository).save(meeting);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"WRITE"})
    void finalizeShouldThrowIfAlreadyFinalized() {
        meeting.setIsFinalized(true);
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));

        assertThatThrownBy(() -> meetingService.finalizeMeeting(1L))
                .isInstanceOf(IllegalStateException.class);
    }
}
