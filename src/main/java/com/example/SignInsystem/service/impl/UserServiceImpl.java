package com.example.SignInsystem.service.impl;

import com.example.SignInsystem.dao.TimeTableDAO;
import com.example.SignInsystem.dao.UserDAO;

import com.example.SignInsystem.entity.UserInfo;
import com.example.SignInsystem.entity.dto.AddUserDTO;
import com.example.SignInsystem.entity.dto.LoginDTO;
import com.example.SignInsystem.entity.dto.ModifyPwDTO;
import com.example.SignInsystem.entity.dto.UserMessage;
import com.example.SignInsystem.enums.BackMessageEnum;
import com.example.SignInsystem.enums.SignInExceptionEnum;
import com.example.SignInsystem.exception.SignInException;
import com.example.SignInsystem.filter.JWTLoginFilter;
import com.example.SignInsystem.filter.UserAuthenticationProvider;
import com.example.SignInsystem.service.UserService;
import com.example.SignInsystem.utils.AuthenticationUtil;
import com.example.SignInsystem.utils.DateUtil;
import com.example.SignInsystem.utils.MD5Util;
import com.example.SignInsystem.utils.ResultVoUtil;
import com.example.SignInsystem.vo.ResultVo;
import com.example.SignInsystem.vo.SingleRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.SignInsystem.utils.MD5Util.passIsCorrect;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author q
 * @Date 18-7-21 下午6:15
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * 初始密码
     */
    private final String INITIAL_PASSWORD = "123456";
    /**
     * 初始身份,即用户身份
     */
    private final String INITIAL_ROLE = "U";
    /**
     * 授权身份
     */
    private final String UPDATE_ROLE = "U,A";
    /**
     * 管理员身份
     */
    private final String ADMIN_ROLE = "A";

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    private JWTLoginFilter jwtLoginFilter = new JWTLoginFilter();

    @Override
    public ResultVo loginT(LoginDTO loginDTO, HttpServletResponse response) {
        log.info("进入UserBasicInfoServiceImpl的userLogin");
        //解析请求,从request中取出authentication
        Authentication authentication = jwtLoginFilter.attemptAuthentication(loginDTO);
        //验证用户名和密码
        Authentication resultAuthentication = userAuthenticationProvider.authenticate(authentication);
        /* 查找用户名，返回 */
        String userName = userDAO.getUserById(loginDTO.getUserId()).getUserName();
        //验证通过，生成token，放入response
        ResultVo resultVo = jwtLoginFilter.successfulAuthentication(resultAuthentication, response, userName);
        return resultVo;
    }

    @Override
    public ResultVo loginR(LoginDTO loginDTO, HttpServletResponse response) {
        log.info("进入UserBasicInfoServiceImpl的userLogin");
        //解析请求,从request中取出authentication
        Authentication authentication = jwtLoginFilter.attemptAuthentication(loginDTO);
        //验证用户名和密码
        Authentication resultAuthentication = userAuthenticationProvider.authenticate(authentication);
        UserInfo user = userDAO.getUserById(loginDTO.getUserId());
        if (!user.getUserRole().contains(ADMIN_ROLE)) {
            throw new SignInException(SignInExceptionEnum.USERID_NOT_EXIT);
        }
        //验证通过，生成token，放入response
        ResultVo resultVo = jwtLoginFilter.successfulAuthentication(resultAuthentication, response, user.getUserName());
        return resultVo;
    }

    @Override
    public synchronized ResultVo addUser(AddUserDTO addUserDTO) {
        UserInfo tempUserInfo = userDAO.getUserById(addUserDTO.getUserId());
        /* 判断要添加的用户是否已经存在 */
        if (!StringUtils.isEmpty(tempUserInfo)) {
            throw new SignInException(SignInExceptionEnum.USERID_HAS_EXIT);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(convertTwoName(addUserDTO.getUserName()));
        userInfo.setUserId(addUserDTO.getUserId());
        /* 初始身份均为用户 */
        userInfo.setUserRole(INITIAL_ROLE);
        /* 数据库中存储的密码 */
        String encodePass = passwordEncoder.encode(INITIAL_PASSWORD);
        userInfo.setPassword(encodePass);
        int i = userDAO.addUser(userInfo);
        if (i != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.ADD_SUCCESS.getMessage());
    }

    @Override
    public ResultVo deleteUser(String userId) {
        int i = userDAO.deleteUser(userId);
        if (i != 0 && i != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.DEL_SUCCESS.getMessage());
    }

    @Override
    public ResultVo getUserList() {

        List<UserInfo> userInfoList = userDAO.getUserList();
        List<UserMessage> userMessageList = new ArrayList<>();

        for (UserInfo userInfo : userInfoList) {
            UserMessage userMessage = new UserMessage();
            userMessage.setUserId(userInfo.getUserId());
            userMessage.setUserName(userInfo.getUserName());
            /* 若是有管理员身份则是1 */
            if (userInfo.getUserRole().contains(ADMIN_ROLE)) {
                userMessage.setUserRole(1);
            } else {
                /* 没有管理员身份为0 */
                userMessage.setUserRole(0);
            }
            userMessageList.add(userMessage);
        }
        return ResultVoUtil.success(userMessageList);
    }

    @Override
    public ResultVo grantAuth(String userId) {
        if (StringUtils.isEmpty(userDAO.getUserById(userId))) {
            throw new SignInException(SignInExceptionEnum.USERID_NOT_EXIT);
        }
        int i = userDAO.updateRole(userId, UPDATE_ROLE);
        if (i != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.GRANT_SUCCESS.getMessage());
    }

    @Override
    public ResultVo cancelAuth(String userId) {
        if (StringUtils.isEmpty(userDAO.getUserById(userId))) {
            throw new SignInException(SignInExceptionEnum.USERID_NOT_EXIT);
        }
        int i = userDAO.updateRole(userId, INITIAL_ROLE);
        if (i != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.CANCEL_SUCCESS.getMessage());
    }

    @Override
    public ResultVo modifyPw(ModifyPwDTO modifyPwDTO) {
        /* 查找用户 */
        UserInfo userInfo = userDAO.getUserById(modifyPwDTO.getUserId());
        /* 原密码错误 */
        if (!passwordEncoder.matches(modifyPwDTO.getOldPw(), userInfo.getPassword())) {
            throw new SignInException(SignInExceptionEnum.OLDPASSWORD_ERROR);
        }
        /* 修改密码 */
        String newPwEncoder = passwordEncoder.encode(modifyPwDTO.getNewPw());
        int i = userDAO.updatePw(modifyPwDTO.getUserId(), newPwEncoder);
        if (i != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.MODIFY_SUCCESS.getMessage());
    }

    @Override
    public ResultVo resetPw(String userId) {
        String encodePassword = passwordEncoder.encode(INITIAL_PASSWORD);
        int i = userDAO.resetPw(userId, encodePassword);
        if (i != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.RESET_SUCCESS.getMessage());
    }

    private String convertTwoName(String userName) {
        String name = userName;
        int nameLength = userName.length();
        if (nameLength == 2) {
            name = userName.substring(0, 1) + " " + userName.substring(1, 2);
        }
        return name;
    }


}
