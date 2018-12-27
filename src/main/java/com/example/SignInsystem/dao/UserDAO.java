package com.example.SignInsystem.dao;

import com.example.SignInsystem.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * userInfo 数据库操作
 * @author q
 */
@Repository
@Mapper
public interface UserDAO {
    /**
     * 获取所有用户列表
     * @return
     */
    List<UserInfo> getUserList();

    /**
     * 查询单个用户 通过id
     * @param userId
     * @return
     */
    UserInfo getUserById(@Param("userId") String userId);

    /**
     * 查询单个用户 通过id
     * @return
     */
    UserInfo getUserByName(@Param("userName") String userName);

    /**
     * 获取userId的用户名
     * @return
     */
    List<String> getUserNameById();

    /**
     * 获取某个用户的名称
     * @param userId
     * @return
     */
    String getNameById(@Param("userId") String userId);

    /**
     * 获取用户ID
     * @param userName
     * @return
     */
    String getUserIdByName(@Param("userName") String userName);

    /**
     * 删除userId的用户
     * @param userId
     * @return
     */
    int deleteUser(@Param("userId") String userId);

    /**
     * 添加用户
     * @param userInfo
     * @return
     */
    int addUser(UserInfo userInfo);
    /**
     * 重置userId用户的密码
     * @param userId
     * @return
     */
    int resetPw(@Param("userId") String userId,@Param("password")String password);

    /**
     * 修改密码
     * @param userId
     * @param password
     * @return
     */
    int updatePw(@Param("userId") String userId,@Param("password") String password);

    /**
     * 修改用户角色
     * @param userId
     * @param userRole
     * @return
     */
    int updateRole(@Param("userId") String userId,@Param("userRole") String userRole);

    int updateTimes(@Param("userId") String userId,@Param("times") int number);




}
