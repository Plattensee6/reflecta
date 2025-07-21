package hu.test.reflecta.auth.repository;

import hu.test.reflecta.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link AppUser} entities.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    /**
     * Retrieves an {@link AppUser} by username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found; otherwise, empty
     */
    Optional<AppUser> findAppUserByusername(String username);

    @Query("""
    SELECT u FROM User u
    WHERE u.id = :id AND (
        :#{hasRole('ADMIN')} = true OR
        u.appUser.username = :#{authentication.name}
    )
""")
    AppUser getReferenceWithAccessById(Long id);

}
