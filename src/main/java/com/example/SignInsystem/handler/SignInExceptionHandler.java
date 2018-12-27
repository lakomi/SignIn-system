package com.example.SignInsystem.handler;

import com.example.SignInsystem.enums.CodeEnum;
import com.example.SignInsystem.exception.SignInException;
import com.example.SignInsystem.utils.ResultVoUtil;
import com.example.SignInsystem.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName SignInExceptionHandler
 * @Description 捕获 SignInException 异常
 * @Author q
 * @Date 18-7-22 下午3:14
 */
@Slf4j
@ControllerAdvice
public class SignInExceptionHandler {

    @ResponseBody
    @ExceptionHandler(SignInException.class)
    public ResultVo handlerSignInException(SignInException e){
        ResultVo resultVo = null;
        resultVo = ResultVoUtil.error(CodeEnum.ERROR.getCode(),e.getMessage());
        return resultVo;
    }



}
