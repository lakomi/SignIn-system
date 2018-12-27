package com.example.SignInsystem.controller;

import com.example.SignInsystem.service.SignInService;
import com.example.SignInsystem.utils.DateUtil;
import com.example.SignInsystem.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName InfoController
 * @Description TODO
 * @Author q
 * @Date 18-8-10 下午12:31
 */
@RestController
@RequestMapping("/info")
@CrossOrigin
@Slf4j
public class InfoController {

    @Autowired
    private SignInService signInService;

    /**
     * 签到
     * @param userId
     * @return
     */
    @GetMapping("/signIn")
    public ResultVo userSignIn(@RequestParam("userId") String userId){
        return signInService.userSignIn(userId);
    }

    /**
     * 签退
     * @param userId
     * @return
     */
    @PostMapping("/signOut")
    public ResultVo userSignOut(@RequestParam("userId") String userId){
        return signInService.userSignOut(userId);
    }

    /**
     * 签到页面  获取签到情况列表
     * @return
     */
    @GetMapping("/getSignList")
    public ResultVo getSignList(){

        String tempDate = DateUtil.getTimeDate();
        return signInService.getSignList(tempDate);

    }






}
