package com.test.mapper;

import com.test.domain.Roles;
import com.test.domain.UserSecurity;

import java.util.List;

/**
 * Created by admin on 2016/7/9.
 */
public interface IUserSecurityDao {
    UserSecurity selectByUsername(String username);

    List<Roles> selectAuthority(String username);
}
