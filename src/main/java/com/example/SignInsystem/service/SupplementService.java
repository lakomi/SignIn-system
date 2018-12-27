package com.example.SignInsystem.service;

import com.example.SignInsystem.entity.dto.SupplementDTO;
import com.example.SignInsystem.vo.ResultVo;

/**
 * @author q
 */
public interface SupplementService {
    /**
     * 提交补签申请
     *
     * @param supplementDTO
     * @return
     */
    ResultVo submitSupple(SupplementDTO supplementDTO);

    /**
     * 管理员 通过补签申请
     *
     * @param supId
     * @return
     */
    ResultVo passSupplement(String supId);

    /**
     * 管理员 拒绝补签
     *
     * @param supId
     * @return
     */
    ResultVo denySupplement(String supId);

    /**
     * 管理员获取未处理的补签列表
     *
     * @return
     */
    ResultVo getSupplementList();

    /**
     * 用户获取个人的补签列表详情
     *
     * @return
     */
    ResultVo getSelfSupList();


}
