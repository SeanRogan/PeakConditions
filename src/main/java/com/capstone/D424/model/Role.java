package com.capstone.D424.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.capstone.D424.model.Permission.*;

@RequiredArgsConstructor
public enum Role {

    ROLE_USER_FREE(Collections.EMPTY_SET),
    ROLE_USER_PAID(Set.of(
            USER_PAID_READ,
            USER_PAID_UPDATE,
            USER_PAID_DELETE,
            USER_PAID_CREATE)),
    ROLE_ADMIN(Set.of(ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            ADMIN_CREATE,
            USER_PAID_READ,
            USER_PAID_UPDATE,
            USER_PAID_DELETE,
            USER_PAID_CREATE));

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}




//
//@Entity
//@Table(name="roles")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Role {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name="role_id")
//    private Long roleId;
//    @Column(name="role_type")
//    private String roleType;
//    @Column(name="create_date")
//    @CreationTimestamp
//    private Date create_date;
//    @Column(name="last_update")
//    @UpdateTimestamp
//    private Date last_update;
//    @ManyToMany(mappedBy = "roles")
//    private Set<User> users;
//}
