package com.example.SignInsystem.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName ModifyPwDTO
 * @Description 修改密码
 * @Author q
 * @Date 18-8-10 上午11:03
 */
@Data
public class ModifyPwDTO {

    @NotBlank(message = "用户名不能为空")
    private String userId;
    @NotBlank(message = "原密码不能为空")
    private String oldPw;
    @NotBlank(message = "新密码不能为空")
    private String newPw;
}
