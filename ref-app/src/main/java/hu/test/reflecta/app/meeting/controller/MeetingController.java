package hu.test.reflecta.app.meeting.controller;

import hu.test.reflecta.auth.service.AuthService;
import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.meeting.data.dto.MeetingResponse;
import hu.test.reflecta.meeting.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
@Tag(name = "Meetings", description = "Meeting management endpoints")
public class MeetingController {
    private final MeetingService meetingService;
    private final AuthService authService;

    @Operation(summary = "Create a new meeting")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Meeting created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(
            @RequestBody MeetingRequest request) {
        var meeting = meetingService.create(request, authService.getCurrentUserId());
        return ResponseEntity.ok(meeting);
    }


    @Operation(summary = "Search meetings with filters")
    @ApiResponse(responseCode = "200", description = "Search meetings with filters and pagination")
    @PreAuthorize("hasAuthority('READ')")
    @GetMapping("/search")
    public ResponseEntity<Page<MeetingResponse>> searchMeetings(
            @Parameter(description = "Meeting title substring") @RequestParam(required = false) String title,
            @Parameter(description = "Start date (ISO format)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date (ISO format)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(description = "Finalized flag") @RequestParam(required = false) Boolean finalized,
            Integer page, Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.DEFAULT_DIRECTION);
        Page<MeetingResponse> results = meetingService.searchMeetings(authService.getCurrentUserId(), title, start, end, finalized, 1001L, 1002L, pageable);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get meeting by ID")
    @PreAuthorize("hasRole('READ')")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getById(
            @Parameter(description = "Meeting ID") @PathVariable Long id
    ) {
        var meeting = meetingService.getById(id);
        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "Delete meeting by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Meeting deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @PreAuthorize("hasAuthority('WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Meeting ID") @PathVariable Long id
    ) {
        meetingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @Operation(summary = "Finalize a meeting")
    @PostMapping("/{id}/finalize")
    public ResponseEntity<Void> finalizeMeeting(
            @Parameter(description = "Meeting ID") @PathVariable Long id
    ) {
        meetingService.finalizeMeeting(id);
        return ResponseEntity.ok().build();
    }
}
