package com.example.SignInsystem.utils;

import com.example.SignInsystem.enums.SignInExceptionEnum;
import com.example.SignInsystem.exception.SignInException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author q
 */
@Slf4j
public class AuthenticationUtil {

    public static String getAuthUserId(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authUserId = (String )authentication.getPrincipal();

        //token失效
        if (authUserId.equals(SignInExceptionEnum.REPEAT_LOGIN.getMessage())){
            log.info("token 失效");
            throw new SignInException(SignInExceptionEnum.REPEAT_LOGIN);
        }
        //token为null(在JWT中已拦截。若token为null，则直接返回403)
        if (authUserId.equals(SignInExceptionEnum.PLEASE_LOGIN_FIRST.getMessage())){
            throw new SignInException(SignInExceptionEnum.PLEASE_LOGIN_FIRST);
        }
        return authUserId;
    }

}
