package com.example.SignInsystem.controller;

import com.example.SignInsystem.entity.dto.AddUserDTO;
import com.example.SignInsystem.enums.CodeEnum;
import com.example.SignInsystem.service.SignInService;
import com.example.SignInsystem.service.SupplementService;
import com.example.SignInsystem.service.UserService;
import com.example.SignInsystem.utils.ResultVoUtil;
import com.example.SignInsystem.vo.ResultVo;
import com.example.SignInsystem.vo.SomeOneTimeVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @ClassName AdminController
 * @Description TODO
 * @Author q
 * @Date 18-7-21 下午7:31
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SignInService signInService;

    @Autowired
    private SupplementService supplementService;

    /**
     * 管理员 重置密码
     *
     * @param userId
     * @return
     */
    @GetMapping("/resetPw")
    public ResultVo ResetPw(@RequestParam("userId") String userId) {
        return userService.resetPw(userId);
    }

    /**
     * 管理员添加用户
     *
     * @param addUserDTO
     * @param bindingResult
     * @return
     */
    @PostMapping("/addUser")
    public ResultVo addUser(@Valid AddUserDTO addUserDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("/login @Valid 注解 验证错误");
            System.out.println(bindingResult.getFieldError().getDefaultMessage());
            return ResultVoUtil.error(CodeEnum.ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        return userService.addUser(addUserDTO);
    }

    /**
     * 管理员删除用户
     *
     * @param userId
     * @return
     */
    @GetMapping("/delUser")
    public ResultVo delUser(@RequestParam("userId") String userId) {
        return userService.deleteUser(userId);
    }

    /**
     * 得到用户列表
     *
     * @return
     */
    @GetMapping("/getUserList")
    public ResultVo getUserList() {
        return userService.getUserList();
    }

    /**
     * 授权
     *
     * @param userId
     * @return
     */
    @PostMapping("/grant")
    public ResultVo grantAuth(@RequestParam("userId") String userId) {
        return userService.grantAuth(userId);
    }

    /**
     * 取消授权
     *
     * @param userId
     * @return
     */
    @PostMapping("/cancelAuth")
    public ResultVo cancelUserAuth(@RequestParam("userId") String userId) {
        return userService.cancelAuth(userId);
    }

    /**
     * 获取所有人在日期范围内的签到时间情况
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/getAllTime")
    public ResultVo getAllTimeList(String startDate, String endDate) {
        return signInService.getAllTimeByDates(startDate, endDate);
    }

    /**
     * 获取某人再日期范围内的签到情况列表
     *
     * @param startDate
     * @param endDate
     * @param userName
     * @return
     */
    @GetMapping("/getOneTime")
    public ResultVo getOneTime(String startDate, String endDate, String userName) {
        SomeOneTimeVo someOneTimeVo = signInService.getOneTime(startDate, endDate, userName);
        return ResultVoUtil.success(someOneTimeVo);
    }

    /**
     * 获取所有人在日期范围内的签到情况列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/getRenyi")
    public ResultVo getRenyiTime(String startDate, String endDate) {
        DateTime start = new DateTime(startDate);
        DateTime end = new DateTime(endDate);

        if (end.isBefore(start.getMillis())) {
            log.info("[查询任意时间]： 输入的开始日期在结束日期之后");
            return signInService.getRenyiTime(endDate, startDate);
        } else {
            log.info("[查询任意时间]： 输入日期正常");
            return signInService.getRenyiTime(startDate, endDate);
        }
    }

    /**
     * 通过补签申请
     *
     * @param supId
     * @return
     */
    @PostMapping("/pass")
    public ResultVo passSupplement(@RequestParam("supId") String supId) {
        return supplementService.passSupplement(supId);
    }

    /**
     * 拒绝补签申请
     *
     * @param supId
     * @return
     */
    @PostMapping("/deny")
    public ResultVo denySupplement(@RequestParam("supId") String supId) {
        return supplementService.denySupplement(supId);
    }

    /**
     * 获取所有等待审批的补签列表
     *
     * @return
     */
    @PostMapping("/getSupList")
    public ResultVo getSupplementList() {
        return supplementService.getSupplementList();
    }


}
