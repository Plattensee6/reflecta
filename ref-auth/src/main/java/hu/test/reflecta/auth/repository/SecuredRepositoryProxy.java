package hu.test.reflecta.auth.repository;

import hu.test.reflecta.auth.exception.SecurityErrorMessages;
import hu.test.reflecta.auth.model.Accessible;
import hu.test.reflecta.auth.model.Role;
import hu.test.reflecta.auth.service.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.function.Predicate;

/**
 * A secured proxy around a {@link JpaRepository} and {@link JpaSpecificationExecutor} to enforce
 * access control based on the current user's permissions.
 * <p>
 * This proxy checks if the current user has access to each entity before returning or modifying it.
 * Optionally, users with {@link Role#ROLE_ADMIN} privileges can be allowed to bypass access checks.
 *
 * @param <T>  the type of the entity, must implement {@link Accessible}
 * @param <ID> the type of the entity's identifier
 */
public class SecuredRepositoryProxy<T extends Accessible, ID> {
    private final JpaRepository<T, ID> repository;
    private final AuthService authService;
    private final SecurityErrorMessages errorMessages;
    private final JpaSpecificationExecutor<T> specificationExecutor;

    public SecuredRepositoryProxy(final JpaRepository<T, ID> repository,
                                  final JpaSpecificationExecutor<T> specExecutor,
                                  final AuthService authService,
                                  final SecurityErrorMessages errorMessages) {
        this.repository = repository;
        this.specificationExecutor = specExecutor;
        this.authService = authService;
        this.errorMessages = errorMessages;
    }

    /**
     * Saves the given entity.
     *
     * @param entity the entity to save
     * @return the persisted entity
     */
    public T save(final T entity) {
        return repository.save(entity);
    }

    /**
     * Retrieves an entity by ID with optional admin access.
     *
     * @param id          the ID of the entity
     * @param allowAdmin  whether admin users can bypass access control
     * @return the entity if found and accessible
     * @throws RuntimeException         if the entity is not found
     * @throws AccessDeniedException   if the user does not have access
     */
    public T getById(final ID id, final boolean allowAdmin) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(errorMessages.entityNotFound()));
        if (isAdmin(allowAdmin)) {
            return entity;
        }
        checkAccess(entity);
        return entity;
    }

    /**
     * Returns all entities in a paginated format without access control.
     *
     * @param pageable the pagination information
     * @return a {@link Page} of entities
     */
    public Page<T> getAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }


    /**
     * Returns all entities, filtering out those the current user cannot access.
     *
     * @param allowAdmin whether admin users can bypass access control
     * @return list of accessible entities
     */
    public List<T> getAll(final boolean allowAdmin) {
        List<T> all = repository.findAll();
        if (isAdmin(allowAdmin)) {
            return all;
        }
        all.forEach(this::checkAccess);
        return all;
    }

    /**
     * Deletes the given entity after verifying access control.
     *
     * @param entity      the entity to delete
     * @param allowAdmin  whether admin users can bypass access control
     * @throws AccessDeniedException if the user does not have permission
     */
    public void delete(final T entity, final boolean allowAdmin) {
        if (!isAdmin(allowAdmin)) {
            checkAccess(entity);
        }
        repository.delete(entity);
    }

    /**
     * Retrieves all entities matching the specification and page, filtering by access if required.
     *
     * @param pageable      the pagination information
     * @param specification the JPA specification
     * @param allowAdmin    whether admin users can bypass access control
     * @return a {@link Page} of entities the user has access to
     */
    public Page<T> getAll(final Pageable pageable,
                          final Specification<T> specification,
                          final boolean allowAdmin) {
        Page<T> all = specificationExecutor.findAll(specification, pageable);
        if (allowAdmin && authService.currentUserHasRole(Role.ROLE_ADMIN)) {
            return all;
        }
        List<T> filtered = all.getContent().stream()
                .filter(filterAccessible())
                .toList();
        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    /**
     * Checks if there are any finalized meetings that overlap based on the specification.
     *
     * @param specification the specification to filter meetings
     * @return {@code true} if any matching finalized meetings exist
     */
    public boolean hasOverlappingFinalizedMeetings(final Specification<T> specification) {
        return specificationExecutor.exists(specification);
    }

    /**
     * Checks if any entity exists matching the given specification.
     *
     * @param specification the JPA specification
     * @return {@code true} if a matching entity exists
     */
    public boolean existsBy(final Specification<T> specification) {
        return specificationExecutor.exists(specification);
    }

    private Predicate<Accessible> filterAccessible() {
        return (accessible -> accessible.hasAccess(authService.getCurrentUsername()));
    }

    private void checkAccess(final T entity) {
        final String currentUser = authService.getCurrentUsername();
        if (!entity.hasAccess(currentUser)) {
            throw new AccessDeniedException(errorMessages.unauthorized());
        }
    }

    private boolean isAdmin(final boolean adminAllowed) {
        return adminAllowed && authService.currentUserHasRole(Role.ROLE_ADMIN);
    }
}
