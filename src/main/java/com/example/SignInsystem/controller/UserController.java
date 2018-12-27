package com.example.SignInsystem.controller;

import com.example.SignInsystem.entity.dto.LoginDTO;
import com.example.SignInsystem.entity.dto.ModifyPwDTO;
import com.example.SignInsystem.entity.dto.SupplementDTO;
import com.example.SignInsystem.enums.CodeEnum;
import com.example.SignInsystem.service.SignInService;
import com.example.SignInsystem.service.SupplementService;
import com.example.SignInsystem.service.UserService;
import com.example.SignInsystem.utils.ResultVoUtil;
import com.example.SignInsystem.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author q
 * @Date 18-7-21 下午6:28
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SignInService signInService;
    @Autowired
    private SupplementService supplementService;

    /**
     * 用户登录
     *
     * @param loginDTO
     * @param bindingResult
     * @param response
     * @return
     */
    @PostMapping("/loginU")
    public ResultVo UserLogin(@Valid LoginDTO loginDTO, BindingResult bindingResult, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
        response.addHeader("Access-Control-Expose-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");

        if (bindingResult.hasErrors()) {
            log.info("/login @Valid 注解 验证错误");
            System.out.println(bindingResult.getFieldError().getDefaultMessage());
            return ResultVoUtil.error(CodeEnum.ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        return userService.loginT(loginDTO, response);
    }

    /**
     * 管理员登录
     *
     * @param loginDTO
     * @param bindingResult
     * @param response
     * @return
     */
    @PostMapping("/loginA")
    public ResultVo AdminLogin(@Valid LoginDTO loginDTO, BindingResult bindingResult, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
        response.addHeader("Access-Control-Expose-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");

        if (bindingResult.hasErrors()) {
            log.info("/login @Valid 注解 验证错误");
            System.out.println(bindingResult.getFieldError().getDefaultMessage());
            return ResultVoUtil.error(CodeEnum.ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        return userService.loginR(loginDTO, response);
    }

    /**
     * 用户查看个人签到记录
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/getSelfTimeInfo")
    public ResultVo getSelfTimeInfo(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        return signInService.getSelfTimeInfo(startDate, endDate);
    }

    /**
     * 获取任意时间段内的打卡记录
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/getSelfRenyi")
    public ResultVo getSelfRenyi(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        return signInService.getSelfRenyi(startDate, endDate);
    }

    /**
     * 修改密码
     *
     * @param modifyPwDTO
     * @return
     */
    @PostMapping("/modifyPw")
    public ResultVo modifyPw(@Valid ModifyPwDTO modifyPwDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("/login @Valid 注解 验证错误");
            System.out.println(bindingResult.getFieldError().getDefaultMessage());
            return ResultVoUtil.error(CodeEnum.ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        return userService.modifyPw(modifyPwDTO);
    }

    /**
     * 提交补签信息
     *
     * @return
     */
    @PostMapping("/submitSup")
    public ResultVo submitSupplement(@Valid SupplementDTO supplementDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("/login @Valid 注解 验证错误");
            System.out.println(bindingResult.getFieldError().getDefaultMessage());
            return ResultVoUtil.error(CodeEnum.ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        return supplementService.submitSupple(supplementDTO);
    }

    /**
     * 获取个人的补签列表
     *
     * @return
     */
    @PostMapping("/getSelfList")
    public ResultVo getSelfSupList() {
        return supplementService.getSelfSupList();
    }

    @GetMapping("/test")
    public ResultVo test() {
        return ResultVoUtil.success("This is a test");
    }


}
