package hu.test.reflecta.auth.model;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER("user"),
    ROLE_ADMIN("admin"),
    ROLE_WRITE("write"),
    ROLE_READ("read");


    private final String roleString;

    Role(String roleString) {
        this.roleString = roleString;
    }
}
