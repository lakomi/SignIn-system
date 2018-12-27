package com.example.SignInsystem.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SomeOneTimeVO
 * @Description TODO
 * @Author q
 * @Date 18-7-30 下午4:36
 */
@Data
public class SomeOneTimeVo {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 单条签到记录
     */
    private List<SingleRecordVo>  singleRecordVoList;
}
