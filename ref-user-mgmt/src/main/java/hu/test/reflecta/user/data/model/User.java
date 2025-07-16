package hu.test.reflecta.user.data.model;

import hu.test.reflecta.auth.model.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "position", nullable = false, columnDefinition = "smallint")
    private Position position;

    @Column(nullable = false)
    private LocalDateTime dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    public void update(final User user) {
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setPosition(user.getPosition());
        this.setDateOfBirth(user.getDateOfBirth());
    }
}