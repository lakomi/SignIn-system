package com.example.SignInsystem.vo;

import lombok.Data;

/**
 * @ClassName SingleRecordVo
 * @Description 单条签到记录
 * @Author q
 * @Date 18-7-23 上午9:30
 */
@Data
public class SingleRecordVo {
    /**
     * 日期
     */
    private String timeDate;
    /**
     * 签到时间
     */
    private String timeIn;
    /**
     * 签退时间
     */
    private String timeOut;
    /**
     * 纪录时长
     */
    private String timeTotal;
    /**
     * 时
     */
    private int hours;
    /**
     * 分
     */
    private int minutes;
    /**
     * 记录是否有效
     */
    private String timeValid;
}
