package hu.test.reflecta.auth.check;

public interface Participant {
    /**
     * Returns whether the user is a participant of the meeting.
     *
     * @param userId current user ID.
     * @return true, if the user is a participant.
     */
    boolean isParticipant(Long userId);
}
