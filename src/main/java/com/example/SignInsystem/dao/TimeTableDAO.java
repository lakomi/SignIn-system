package com.example.SignInsystem.dao;

import com.example.SignInsystem.entity.TimeTable;
import com.example.SignInsystem.vo.SignListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * @author q
 */
@Repository
@Mapper
public interface TimeTableDAO {
    /**
     * 通过学号、日期查询日期内的签到签退情况
     * 从本周表中得到数据
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    List<TimeTable> getTablesByDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userId") String userId);

    /**
     * 获取新表中某人的某小日期
     *
     * @param userId
     * @return
     */
    String getNewTableMinTime(@Param("userId") String userId);

    /**
     * 获取旧表中某人的最大日期
     *
     * @param userId
     * @return
     */
    String getOldTableMaxTime(@Param("userId") String userId);

    /**
     * 通过学号、日期查询日期内的签到签退情况
     * 从五周表中得到数据
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    List<TimeTable> getAgoTableByDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userId") String userId);

    /**
     * 添加签到信息
     *
     * @param timeId
     * @param userId
     * @param timeDate
     * @param timeIn
     * @param timeState
     * @return
     */
    int addTimeTable(@Param("timeId") String timeId, @Param("userId") String userId, @Param("timeDate") String timeDate, @Param("timeIn") String timeIn, @Param("timeState") int timeState);

    /**
     * 补充签退信息
     *
     * @param timeId
     * @param timeOut
     * @param timeState
     * @param timeValid
     * @return
     */
    int updateTimeTable(@Param("timeId") String timeId, @Param("timeOut") String timeOut, @Param("timeState") int timeState, @Param("timeValid") String timeValid);

    /**
     * 批量插入数据
     *
     * @param timeTableList
     * @return
     */
    int insertListToAgoTable(@Param("timeTableList") List<TimeTable> timeTableList);

    /**
     * 某天 所有用户的签到签退状态列表
     *
     * @param tempDate
     * @return
     */
    List<SignListVo> getSignListByDate(@Param("tempDate") String tempDate);

    /**
     * 定时任务  将所有签退时间为空的记录的状态设为签到可用，并该条记录设为无效
     *
     * @return
     */
    int timerUpdate();

    /**
     * 查找数据库中是否已有待签退的记录
     *
     * @param userId
     * @param tempDate
     * @return
     */
    List<TimeTable> isRepeat(@Param("userId") String userId, @Param("tempDate") String tempDate);

    /**
     * 在某一时间内，查询所有
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<TimeTable> selectAll(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 在某一时间内，删除所有
     *
     * @param startDate
     * @param endDate
     * @return
     */
    int deleteAll(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 删除某一天之前的签到记录
     *
     * @return
     */
    int deleteOneDayAgo();

    /**
     * 插入签到记录
     *
     * @param timeTable
     * @return
     */
    int insertOneTimeSup(TimeTable timeTable);


}
