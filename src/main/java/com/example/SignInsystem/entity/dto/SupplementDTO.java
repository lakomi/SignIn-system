package com.example.SignInsystem.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName SupplementDTO
 * @Description TODO
 * @Author q
 * @Date 18-12-3 下午10:05
 */
@Data
public class SupplementDTO {
    /**
     * 补签理由
     */
    @NotBlank(message = "补签理由不能为空")
    private String applyExcuse;
    /**
     * 补签开始时间
     */
    @NotBlank(message = "补签起始时间不能为空")
    private String applyTime;
    /**
     * 补签日期
     */
    @NotBlank(message = "补签日期不能为空")
    private String applyDate;
}
