package hu.test.reflecta.user.data.repository;

import hu.test.reflecta.user.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM User u WHERE u.appUser.id = :appUserId")
    Optional<User> findByAppUserId(@Param("appUserId") Long appUserId);


    @Query("""
                SELECT u FROM User u
                WHERE u.id = :id AND (
                    :#{hasRole('ADMIN')} = true OR
                    u.appUser.username = :#{authentication.name}
                )
            """)
    Optional<User> findByEmail(String email);

    @Query("""
                SELECT u FROM User u
                WHERE u.id = :id AND (
                    :#{hasRole('ADMIN')} = true OR
                    u.appUser.username = :#{authentication.name}
                )
            """)
    Optional<User> findById(Long id);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE u.email = :email")
    Boolean existsByEmail(@Param("email") String email);
}
