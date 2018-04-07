package ru.saidgadjiev.overtalk.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.saidgadjiev.overtalk.application.domain.UserProfile;
import ru.saidgadjiev.overtalk.application.domain.UserRole;
import ru.saidgadjiev.overtalk.application.model.ExtSpringUser;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by said on 18.03.2018.
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            UserProfile userProfile = userService.getByUserName(s);
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

            for (UserRole userRole : userProfile.getUserRoles()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
            }

            return new ExtSpringUser(
                    userProfile.getNickName(),
                    userProfile.getUserName(),
                    userProfile.getPassword(),
                    grantedAuthorities
            );
        } catch (SQLException ex) {
            throw new UsernameNotFoundException(ex.getMessage(), ex);
        }
    }
}
