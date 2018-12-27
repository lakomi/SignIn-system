package com.example.SignInsystem.vo;

import lombok.Data;

/**
 * @ClassName AllTimeListVo
 * @Description TODO
 * @Author q
 * @Date 18-7-29 下午5:14
 */
@Data
public class AllTimeListVo {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 签到次数
     */
    private int timeFre;
    /**
     * 平均时间 时
     */
    private int AHours;
    /**
     * 平均时间 分
     */
    private int AMinutes;
    /**
     * 签到总时长
     */
    private String totalTime;
    /**
     * 总时
     */
    private int totalHours;
    /**
     * 总分
     */
    private int totalMinutes;
    /**
     * 是否有效
     */
    private String isQualified;
    /**
     * 连续不合格次数
     */
    private Integer unQualifyTimes;
}
