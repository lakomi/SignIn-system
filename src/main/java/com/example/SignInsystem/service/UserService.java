package com.example.SignInsystem.service;

import com.example.SignInsystem.entity.dto.AddUserDTO;
import com.example.SignInsystem.entity.dto.LoginDTO;
import com.example.SignInsystem.entity.dto.ModifyPwDTO;
import com.example.SignInsystem.vo.ResultVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author q
 */
public interface UserService {

    /**
     * 用户登录
     * @param loginDTO
     * @param response
     * @return
     */
    ResultVo loginT(LoginDTO loginDTO,HttpServletResponse response);

    /**
     * 管理员登录
     * @param loginDTO
     * @param response
     * @return
     */
    ResultVo loginR(LoginDTO loginDTO,HttpServletResponse response);

    /**
     * 管理员添加用户
     * @param addUserDTO
     * @return
     */
    ResultVo addUser(AddUserDTO addUserDTO);

    /**
     * 管理员删除用户
     * @param userId
     * @return
     */
    ResultVo deleteUser(String userId);

    /**
     * 获取所有成员信息
     * @return
     */
    ResultVo getUserList();

    /**
     * 授权
     * @param userId
     * @return
     */
    ResultVo grantAuth(String userId);

    /**
     * 取消授权
     * @param userId
     * @return
     */
    ResultVo cancelAuth(String userId);

    /**
     * 修改密码
     * @param modifyPwDTO
     * @return
     */
    ResultVo modifyPw(ModifyPwDTO modifyPwDTO);

    /**
     * 重置密码
     * @param userId
     * @return
     */
    ResultVo resetPw(String userId);

}
