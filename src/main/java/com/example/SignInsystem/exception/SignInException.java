package com.example.SignInsystem.exception;

import com.example.SignInsystem.enums.SignInExceptionEnum;
import lombok.Data;

/**
 * @ClassName SignInException
 * @Description TODO
 * @Author q
 * @Date 18-7-21 下午5:59
 */
@Data
public class SignInException extends RuntimeException {
    private Integer code;

    public SignInException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public SignInException(SignInExceptionEnum signInExceptionEnum) {
        super(signInExceptionEnum.getMessage());
        this.code = signInExceptionEnum.getCode();
    }




}
