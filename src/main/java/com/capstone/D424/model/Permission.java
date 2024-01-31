package com.capstone.D424.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    USER_PAID_READ("management:read"),
    USER_PAID_UPDATE("management:update"),
    USER_PAID_CREATE("management:create"),
    USER_PAID_DELETE("management:delete")

    ;

    @Getter
    private final String permission;
}
