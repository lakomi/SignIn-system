package com.example.SignInsystem.dao;

import com.example.SignInsystem.entity.Supplement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author q
 */
@Repository
@Mapper
public interface SupplementDAO {
    /**
     * 获取所有等待通过的申请
     *
     * @param isPass
     * @return
     */
    List<Supplement> getAllSupplementByIsPass(@Param("isPass") int isPass);


    /**
     * 查询某个用户的补签列表
     *
     * @param userId
     * @return
     */
    List<Supplement> getSomeOneSupple(@Param("userId") String userId);

    /**
     * 插入补签记录
     *
     * @param supplement
     * @return
     */
    int insertOneSupple(Supplement supplement);

    /**
     * 更新补签记录情况
     *
     * @param supId
     * @param isPass
     * @return
     */
    int updateOneSupple(@Param("supId") String supId, @Param("isPass") int isPass);

    /**
     * 获取某一条记录
     *
     * @param supId
     * @return
     */
    Supplement getOne(@Param("supId") String supId);


}
