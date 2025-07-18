package hu.test.reflecta.user.data.repository;

import hu.test.reflecta.user.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.appUser.id = :appUserId")
    Optional<User> findByAppUserId(@Param("appUserId") Long appUserId);

    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE u.email = :email")
    Boolean existsByEmail(@Param("email") String email);
}
