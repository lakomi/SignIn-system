package com.example.SignInsystem.entity.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName AddUserDTO
 * @Description TODO
 * @Author q
 * @Date 18-7-21 下午7:09
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddUserDTO {

    @NotBlank(message = "学号不能为空")
    private String userId;

    @NotBlank(message = "姓名不能为空")
    private String userName;


}
