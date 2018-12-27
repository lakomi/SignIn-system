package com.example.SignInsystem.entity;
import lombok.Data;
/**
 * @ClassName TimeTable
 * @Description 签到签退时间表
 * @Author q
 * @Date 18-7-22 下午2:31
 */
@Data
public class TimeTable {
    /**
     * 时间表编号  15位
     */
    private String timeId;
    /**
     * 学号
     */
    private String userId;
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
     * 状态   0签到   1签退
     */
    private int timeState;
    /**
     * 签到签退是否有效
     */
    private String timeValid;
}
