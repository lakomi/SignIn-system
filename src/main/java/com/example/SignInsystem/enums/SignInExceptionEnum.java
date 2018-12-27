package com.example.SignInsystem.enums;

import lombok.Getter;

@Getter
public enum SignInExceptionEnum {
    /**
     * token过期 需要重新登录
     */
    REPEAT_LOGIN(2, "请重新登录"),
    /**
     * token为空 先登录
     */
    PLEASE_LOGIN_FIRST(16, "请先登录"),
    /**
     * 用户名不存在
     */
    USERID_NOT_EXIT(1, "用户名不存在"),
    /**
     * UserAuthenticationProvider 中提取用户详情出错
     */
    GET_USERDETAILS_ERROR(3, "登录中获取用户详情出错"),
    /**
     * 密码错误
     */
    PASSWORD_ERROR(9, "密码错误"),
    /**
     * 服务器内部错误，数据库操作出错
     */
    SQL_ERROR(4, "增山查改操作数据库出错"),
    /**
     * 签到签退的日期不同
     */
    SIGNIN_OUT(5, "该条签到记录已过期，不能签退"),
    /**
     * 添加用户，判断是否存在
     */
    USERID_HAS_EXIT(6, "该用户已存在"),
    /**
     * 修改密码，原密码错误
     */
    OLDPASSWORD_ERROR(7, "原密码错误"),
    /**
     * 签到记录存在当天未签退，就签到
     */
    SIGNIN_ISREPEAT(8, "有未签到的记录，请先签退"),
    /**
     * 补签日期提交错误
     */
    SUPPLEMENT_DATE_ERROR(9, "补签日期只能在本周之内"),;

    private Integer code;

    private String message;

    SignInExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
