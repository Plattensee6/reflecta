package hu.test.reflecta.auth.check;

/**
 * Defines the contract for checking if a user is a participant in a meeting.
 */
public interface Participant {
    /**
     * Returns whether the user is a participant of the meeting.
     *
     * @param userId current user ID.
     * @return true, if the user is a participant.
     */
    boolean isParticipant(Long userId);
}
