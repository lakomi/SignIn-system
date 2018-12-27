package com.example.SignInsystem.entity.dto;

import lombok.Data;

/**
 * token 传输对象
 * @author q
 */
@Data
public class TokenDTO {

    /**
     * token
     */
    private String authorization;
    /**
     * 姓名
     */
    private String userName;
}
