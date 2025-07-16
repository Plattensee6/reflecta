package hu.test.reflecta.meeting.data.model;

import hu.test.reflecta.auth.model.Accessible;
import hu.test.reflecta.meeting.data.dto.MeetingRequest;
import hu.test.reflecta.user.data.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_meeting")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting implements Accessible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    @NotNull(message = "End date/time is mandatory")
    private LocalDateTime endDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private User employee;

    @Column(nullable = false)
    private Boolean isFinalized = false;

    public void finalizeMeeting() {
        this.isFinalized = true;
    }

    public void update(final MeetingRequest dto) {
        this.setDescription(dto.getDescription());
        this.setTitle(dto.getTitle());
        this.setIsFinalized(dto.getIsFinalized());
    }

    @Override
    public Boolean hasAccess(final String currUserName) {
        return (manager.getAppUser().getUsername().equals(currUserName)
                || employee.getAppUser().getUsername().equals(currUserName));
    }
}
