package com.example.SignInsystem.entity;

import lombok.Data;

/**
 * @ClassName UserInfo
 * @Description 用户详情
 * @Author q
 * @Date 18-7-20 下午6:18
 */
@Data
public class UserInfo {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户角色
     */
    private String userRole;
    /**
     * 连续不合格次数
     */
    private Integer unQualifyTimes;
}
