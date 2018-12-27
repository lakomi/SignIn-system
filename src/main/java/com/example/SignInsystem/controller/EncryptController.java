package com.example.SignInsystem.controller;

import com.example.SignInsystem.utils.ComputerUtil;
import com.example.SignInsystem.utils.DESUtil;
import com.example.SignInsystem.utils.TXTUtil;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName EncryptController
 * @Description TODO
 * @Author q
 * @Date 18-9-6 下午3:32
 */
@RestController
@RequestMapping("/en")
@CrossOrigin
public class EncryptController {

    @GetMapping("/check")
    public static String checkIdentify(){
        String data = TXTUtil.readContent();
        String localMac = ComputerUtil.getMacAddressByIp();
//        String localMac = "68-07-15-1a-42-03";
        System.out.println("localMac"+localMac);
        String localMacString = TXTUtil.convertMac(localMac);
        Boolean flag = DESUtil.checkMac(data,localMacString);
        System.out.println(flag);
        return flag.toString();
    }

}
