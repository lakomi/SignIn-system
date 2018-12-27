package com.example.SignInsystem.entity;

import lombok.Data;

/**
 * @ClassName Supplement
 * @Description TODO
 * @Author q
 * @Date 18-12-3 下午9:06
 */
@Data
public class Supplement {
    /**
     * 补签Id
     */
    private String supId;
    /**
     * 申请补签用户id
     */
    private String userId;
    /**
     * 申请补签理由
     */
    private String applyExcuse;
    /**
     * 申请补签日期
     */
    private String applyDate;
    /**
     * 补签的起始时间
     */
    private String applyTime;
    /**
     * 是否允许
     */
    private Integer isPass;


}
