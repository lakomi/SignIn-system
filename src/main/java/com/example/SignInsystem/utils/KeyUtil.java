package com.example.SignInsystem.utils;

import java.util.Random;

/**
 * @ClassName KeyUtil
 * @Description 生成签到表主键编号
 * @Author q
 * @Date 18-7-22 下午3:17
 */
public class KeyUtil {

    /**
     * 生成主键
     * 时间+3位随机数
     * @return
     */
    public static synchronized String getUniqueKey(){
        Random random = new Random();
        Integer number = random.nextInt(90)+10;
        return System.currentTimeMillis()+String.valueOf(number);
    }






}
