package com.example.SignInsystem.service.impl;


import com.example.SignInsystem.dao.UserDAO;
import com.example.SignInsystem.entity.UserGrantedAuthority;
import com.example.SignInsystem.entity.UserInfo;
import com.example.SignInsystem.enums.SignInExceptionEnum;
import com.example.SignInsystem.exception.SignInException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 返回用户名和密码及权限
 * @author q
 */
@Slf4j
@Component
public class MyUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        //根据用户名查找用户信息
        UserInfo userInfo = userDAO.getUserById(userId);
        if(StringUtils.isEmpty(userInfo)){
            throw new SignInException(SignInExceptionEnum.USERID_NOT_EXIT);
        }
        String password = userInfo.getPassword();
        //GrantedAuthority是security提供的权限类，
        List<UserGrantedAuthority> grantedAuthorityList = new ArrayList<UserGrantedAuthority>();
        //获取该用户角色，并放入list中
        getRoles(userInfo,grantedAuthorityList);
        return new User(userId,password,grantedAuthorityList);
    }

    public void getRoles(UserInfo userInfo,List<UserGrantedAuthority> list){
        for (String role:userInfo.getUserRole().split(",")) {
            //权限如果前缀是ROLE_，security就会认为这是个角色信息，而不是权限，例如ROLE_MENBER就是MENBER角色，CAN_SEND就是CAN_SEND权限
             list.add(new UserGrantedAuthority("ROLE_"+role));
        }
    }


}
