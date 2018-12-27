package com.example.SignInsystem.utils;

import com.example.SignInsystem.dao.TimeTableDAO;
import com.example.SignInsystem.dao.UserDAO;
import com.example.SignInsystem.entity.TimeTable;
import com.example.SignInsystem.entity.UserInfo;
import com.example.SignInsystem.service.SignInService;
import com.example.SignInsystem.vo.AllTimeListVo;
import com.example.SignInsystem.vo.ResultVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName TimedTask
 * @Description 定时任务
 * @Author q
 * @Date 18-7-23 下午3:44
 */
@Component
public class TimedTask {
    /**
     * 查询日期中，开始日期，是today日期的前几天
     */
    private static final int AGO_START = 7;
    /**
     * 查询日期中，结束日期，是today日期的前几天
     */
    private static final int AGO_END = 1;
    /**
     * 删除几天前的签到记录
     */
    private static final int AGO_TEMP = 35;

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TimeTableDAO timeTableDAO;
    @Autowired
    private SignInService signInService;

    /**
     * 每天零点将数据库中未签退的记录设为无效
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void timerTask() {
        DateTime today = new DateTime();
        System.out.println("当前时间为：" + today.toString("yyyy-MM-dd HH:mm:ss"));
        try {
            int i = timeTableDAO.timerUpdate();
            System.out.println("[定时任务]: 无签退的更新记录有" + i + "条");
        } catch (Exception e) {
            System.out.println("[定时任务]: 操作数据库出错");
            e.printStackTrace();
        }
        System.out.println("[定时任务]: 结束");
    }

    /**
     * 每周日 将原本周的签到信息记录移动到旧时间表中，并删除五周之前的签到记录
     */
    @Transactional
    @Scheduled(cron = "0 30 0 ? * MON")
    public void moveTableTask() {
        DateTime today = new DateTime();
        System.out.println("当前时间为： " + today.toString("yyyy-MM-dd HH:mm:ss"));
        String startDate = today.minusDays(AGO_START).toString("yyyy-MM-dd");
        String endDate = today.minusDays(AGO_END).toString("yyyy-MM-dd");
        String tempDate = today.minusDays(AGO_TEMP).toString("yyyy-MM-dd");
        System.out.println("[移动数据定时任务]： 时间段为" + startDate + "～" + endDate);
        List<TimeTable> timeTableList = timeTableDAO.selectAll(startDate, endDate);
        try {
            if (timeTableList.size() != 0) {
                int i = timeTableDAO.insertListToAgoTable(timeTableList);
                System.out.println("[移动数据定时任务]: 插入旧表共完成" + i + "条记录");
                int j = timeTableDAO.deleteAll(startDate, endDate);
                System.out.println("[移动数据定时任务]: 删除新表共完成" + j + "条记录");
            } else {
                System.out.println("[移动数据定时任务]: 插入旧表共完成0条记录");
            }
//            System.out.println("[定时任务]: 删除旧表五周前数据，即"+tempDate+"前数据,不包括");
//            /* 删除5周之前的签到记录信息 */
//            int k = timeTableDAO.deleteOneDayAgo();
//            System.out.println("[定时任务]: 删除旧表共完成"+ k + "条记录");
        } catch (Exception e) {
            System.out.println("[移动数据定时任务]: 操作数据库出错");
            e.printStackTrace();
        }
        System.out.println("[移动数据定时任务]: 结束");

        System.out.println("[不合格处理定时任务]：开始");
        System.out.println("[不合格处理定时任务]: 时间日期位为" + today.minusDays(7).toString("yyyy-MM-dd") + "~" + today.minusDays(1).toString("yyyy-MM-dd"));

        ResultVo resultVo = signInService.getAllTimeByDates(today.minusDays(7).toString("yyyy-MM-dd"), today.minusDays(1).toString("yyyy-MM-dd"));
        List<AllTimeListVo> allTimeListVoList = (List<AllTimeListVo>) resultVo.getData();
        for (int i = 0; i < allTimeListVoList.size(); i++) {
            if ("N".equals(allTimeListVoList.get(i).getIsQualified())) {
                UserInfo userInfo = userDAO.getUserByName(allTimeListVoList.get(i).getUserName());
                int number = userInfo.getUnQualifyTimes() + 1;
                userDAO.updateTimes(userInfo.getUserId(), number);
            } else {
                UserInfo userInfo = userDAO.getUserByName(allTimeListVoList.get(i).getUserName());
                int number = 0;
                userDAO.updateTimes(userInfo.getUserId(), number);
            }
        }
    }
}
