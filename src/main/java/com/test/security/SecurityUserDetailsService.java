package com.test.security;

import com.test.domain.Roles;
import com.test.domain.UserSecurity;
import com.test.mapper.IUserSecurityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/7/9.
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {
    @Autowired
    private IUserSecurityDao userSecurityDao;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserSecurity userSecurity = userSecurityDao.selectByUsername(username);
        List<Roles> rolesList = userSecurityDao.selectAuthority(username);
        if (rolesList == null || rolesList.size() == 0)
            throw new UsernameNotFoundException("user is not found");
        List<GrantedAuthority> authority = new ArrayList<>();
        for (Roles roles : rolesList) {
            authority.add(new SimpleGrantedAuthority(roles.getRoleName()));
        }
        return new User(userSecurity.getUsername(), userSecurity.getPwd(), authority);
    }

}
