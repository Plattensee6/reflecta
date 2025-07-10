package hu.test.reflecta.auth.model;

public interface Accessible {
    Boolean hasAccess(Long currUserId);
}
