package hu.test.reflecta.meeting.data.repository;

import hu.test.reflecta.auth.check.RequireAccess;
import hu.test.reflecta.meeting.data.model.Meeting;
import hu.test.reflecta.meeting.data.spec.MeetingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long>, JpaSpecificationExecutor<Meeting> {
    @Query("""
                SELECT COUNT(m) > 0 FROM Meeting m
                WHERE m.isFinalized = true
                  AND m.id <> :id
                  AND (
                    m.employee.id = :employeeId
                    OR m.manager.id = :managerId
                  )
                  AND m.startDateTime < :endTime
                  AND m.endDateTime > :startTime
            """)
    @RequireAccess(allowAdmin = true)
    boolean hasOverlappingFinalizedMeetings(
            @Param("id") Long id,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime,
            @Param("employeeId") Long employeeId,
            @Param("managerId") Long managerId
    );

    @RequireAccess(allowAdmin = true)
    Optional<Meeting> getReferenceWithAccessById(Long id);

    @RequireAccess(allowAdmin = true)
    void deleteWithAccessById(Long id);

    @RequireAccess
    Page<Meeting> findAllWithAccess(Specification<MeetingSpecification> spec, Pageable pageable);
}
