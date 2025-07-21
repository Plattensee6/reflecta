package hu.test.reflecta.auth.model;

/**
 * Represents an entity that can determine whether a given user has access to it.
 * Implementations of this interface define custom access control logic based on the
 * provided username (typically the currently authenticated user).
 */
public interface Accessible {
    /**
     * Checks if the specified user has access to the implementing object.
     *
     * @param currUserName the username of the user whose access is being checked
     * @return {@code true} if the user has access; {@code false} otherwise
     */
    Boolean hasAccess(String currUserName);
}
