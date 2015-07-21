package it.sevenbits.springboottutorial.web.utils;

import it.sevenbits.springboottutorial.core.domain.Role;
import it.sevenbits.springboottutorial.core.domain.User;
import it.sevenbits.springboottutorial.web.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserResolver {
    @Autowired
    private UsersService usersService;

    public String getUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Role.ROLE_ANONIMOUS.getName();
        }

        return ((GrantedAuthority)(auth.getAuthorities().toArray()[0])).getAuthority();
    }

    public Boolean isUserInRole(String roleName) {
        return getUserRole().equals(roleName);
    }

    public String getUsername() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {

            return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }
        return null;
    }
}
