package sn.uasz.guidetouristique.security;

import java.util.Arrays;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sn.uasz.guidetouristique.domain.Role;

@Component
public class CurrentUser {

    private Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String username() {
        return auth().getName();
    }

    public boolean hasRole(Role role) {
        return auth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
    }

    public boolean hasAnyRole(Role... roles) {
        return Arrays.stream(roles).anyMatch(this::hasRole);
    }
}
