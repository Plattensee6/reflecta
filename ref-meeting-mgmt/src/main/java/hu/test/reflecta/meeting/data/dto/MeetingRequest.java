package hu.test.reflecta.meeting.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRequest {
    @NotNull(message = "Manager ID is required")
    private Long managerId;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 500, message = "Description must be between 3 and 200 characters")
    private String description;

    @NotNull(message = "Start date/time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End date/time is required")
    private LocalDateTime endTime;

    private Boolean isFinalized;
}
