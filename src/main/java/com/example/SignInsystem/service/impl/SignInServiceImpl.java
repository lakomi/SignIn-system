package com.example.SignInsystem.service.impl;

import com.example.SignInsystem.dao.TimeTableDAO;
import com.example.SignInsystem.dao.UserDAO;
import com.example.SignInsystem.entity.TimeTable;
import com.example.SignInsystem.entity.UserInfo;
import com.example.SignInsystem.enums.BackMessageEnum;
import com.example.SignInsystem.enums.SignInExceptionEnum;
import com.example.SignInsystem.exception.SignInException;
import com.example.SignInsystem.service.SignInService;
import com.example.SignInsystem.utils.*;
import com.example.SignInsystem.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SignInServiceImpl
 * @Description TODO
 * @Author q
 * @Date 18-7-22 下午8:04
 */
@Slf4j
@Service
public class SignInServiceImpl implements SignInService {
    /**
     * 为0时，签到可用
     */
    private final int SIGN_IN = 0;
    /**
     * 为1时，签退可用
     */
    private final int SIGN_OUT = 1;
    /**
     * 签到记录有效
     */
    private final String VALID = "Y";
    /**
     * 签到记录无效
     */
    private final String UNVALID = "N";
    /**
     * 该条记录为补签且通过
     */
    private final String SUPP = "B";
    /**
     * 该条补签拒绝
     */
    private final String SUPP_NOT_PASS = "R";
    /**
     * 每周应签到时长
     */
    private final int PRESCRIBEDTIME = 35;
    /**
     * 大一每周签到时间
     */
    private final int FRASHMAN = 28;
    /**
     * 允许的平均签到时长
     */
    private final int AVERAGETIMEHOUR = 8;

    @Autowired
    private TimeTableDAO timeTableDAO;
    @Autowired
    private UserDAO userDAO;

    @Override
    public synchronized ResultVo userSignIn(String userId) {
        String timeDate = DateUtil.getTimeDate();
        List<TimeTable> timeTableList = timeTableDAO.isRepeat(userId, timeDate);
        System.out.println(timeTableList.toString());
        if (timeTableList.size() == 0) {
            String timeId = KeyUtil.getUniqueKey();
            String timeIn = DateUtil.getTimeInfo();
            /* 签到之后，签退可用 */
            int i = timeTableDAO.addTimeTable(timeId, userId, timeDate, timeIn, SIGN_OUT);
            if (i != 1) {
                throw new SignInException(SignInExceptionEnum.SQL_ERROR);
            }
            return ResultVoUtil.success(BackMessageEnum.SIGNIN_SUCCESS.getMessage(), timeId);
        } else {
            return ResultVoUtil.error(2, SignInExceptionEnum.SIGNIN_ISREPEAT.getMessage());
        }
    }

    @Override
    public synchronized ResultVo userSignOut(String userId) {
        ResultVo resultVo = ResultVoUtil.error(2, "签退失败");
        String tempDate = DateUtil.getTimeDate();
        List<TimeTable> timeTables = timeTableDAO.isRepeat(userId, tempDate);
        for (int i = 0; i < timeTables.size(); i++) {
            /* 判断签退与签到是否是同一天 */
            if (!tempDate.equals(timeTables.get(i).getTimeDate())) {
                throw new SignInException(SignInExceptionEnum.SIGNIN_OUT);
            }
            String timeOut = DateUtil.getTimeInfo();
            String signDiff = calculateTimeLen(timeTables.get(i).getTimeIn(), timeOut);
            System.out.println("时差为" + signDiff);
            String status = "";
            if (!"0-0".equals(signDiff)) {
                log.info("[签退]： 签退有效");
                status = VALID;
            } else {
                log.info("[签退]： 时间太短，签退无效");
                status = UNVALID;
            }
            /* 签退之后，签到可用 */
            int j = timeTableDAO.updateTimeTable(timeTables.get(i).getTimeId(), timeOut, SIGN_IN, status);
            if (j != 1) {
                throw new SignInException(SignInExceptionEnum.SQL_ERROR);
            }
            resultVo = ResultVoUtil.success(BackMessageEnum.SIGNOUT_SUCCESS.getMessage());
        }
        return resultVo;
    }

    @Override
    public ResultVo getSelfTimeInfo(String startDate, String endDate) {
        /* 用户id */
        String userId = AuthenticationUtil.getAuthUserId();
        List<TimeTable> timeTableList = getTimeTableList(startDate, endDate, userId);
        UserInfo userInfo = userDAO.getUserById(userId);
        /* 拼装返回信息 */
        TimeTableVo timeTableVo = new TimeTableVo();
        List<SingleRecordVo> singleRecordVoList = togetherData(timeTableList);
        timeTableVo.setSingleRecordVoList(singleRecordVoList);
        /* 计算总签到时长 */
        String totalTime = calculateTotalTime(singleRecordVoList);
        timeTableVo.setTotalTime(totalTime);
        /* 合格次数 */
        int count = 0;
        for (int i = 0; i < singleRecordVoList.size(); i++) {
            if (VALID.equals(singleRecordVoList.get(i).getTimeValid()) || SUPP.equals(singleRecordVoList.get(i).getTimeValid())) {
                count++;
            }
        }
        String averageTime = DateUtil.calculateAverageTime(totalTime, count);
        timeTableVo.setAverageTime(averageTime);
        timeTableVo.setIsQualified(isQualify(userId, totalTime, averageTime));
        timeTableVo.setUnQualifyTimes(userInfo.getUnQualifyTimes());
        return ResultVoUtil.success(timeTableVo);
    }

    @Override
    public ResultVo getSignList(String tempDate) {
        List<SignListVo> signListVoList = timeTableDAO.getSignListByDate(tempDate);
        for (SignListVo signListVo : signListVoList) {
            /* 设置签到可用 */
            if (signListVo.getTimeState() != SIGN_OUT) {
                signListVo.setTimeState(SIGN_IN);
            }
        }
        return ResultVoUtil.success(signListVoList);
    }

    @Override
    public ResultVo getAllTimeByDates(String startDate, String endDate) {
        /* 用户名单 */
        List<String> userNameList = userDAO.getUserNameById();
        /* 签到有效次数 */
        int timeFre = 0;
        String totalTime = "0-0";
        List<AllTimeListVo> allTimeListVoList = new ArrayList<>();
        /* 循环用户，拼装返回信息 */
        for (String userName : userNameList) {
            /* 不显示张三 */
            if ("测试_张三".equals(userName)) {
                continue;
            }
            timeFre = 0;
            totalTime = "0-0";
            /* 由用户名获取userId */
            UserInfo userInfo = userDAO.getUserByName(userName);
            AllTimeListVo allTimeListVo = new AllTimeListVo();
            allTimeListVo.setUserName(userName);
            /* 查询一个人的在日期内的签到情况 */
            SomeOneTimeVo someOneTimeVo = getOneTime(startDate, endDate, userName);
            /* 循环遍历签到有效次数 */
            for (int i = 0; i < someOneTimeVo.getSingleRecordVoList().size(); i++) {
                if (VALID.equals(someOneTimeVo.getSingleRecordVoList().get(i).getTimeValid()) || SUPP.equals(someOneTimeVo.getSingleRecordVoList().get(i).getTimeValid())) {
                    timeFre++;
                }
            }
            allTimeListVo.setTimeFre(timeFre);
            /* 计算某人所有记录的总时间 */
            totalTime = calculateTotalTime(someOneTimeVo.getSingleRecordVoList());
            allTimeListVo.setTotalTime(totalTime);
            String[] temptotal = totalTime.split("-");
            allTimeListVo.setTotalHours(Integer.parseInt(temptotal[0]));
            allTimeListVo.setTotalMinutes(Integer.parseInt(temptotal[1]));
            /* 计算某人的平均时间 */
            String averageTime = DateUtil.calculateAverageTime(totalTime, timeFre);
            String[] tempA = averageTime.split("-");
            allTimeListVo.setAHours(Integer.parseInt(tempA[0]));
            allTimeListVo.setAMinutes(Integer.parseInt(tempA[1]));
            /* 该时间段打卡是否合格 */
            allTimeListVo.setIsQualified(isQualify(userInfo.getUserId(), totalTime, averageTime));
            allTimeListVo.setUnQualifyTimes(userInfo.getUnQualifyTimes());
            allTimeListVoList.add(allTimeListVo);

        }
        return ResultVoUtil.success(allTimeListVoList);
    }

    @Override
    public SomeOneTimeVo getOneTime(String startDate, String endDate, String userName) {
        String userId = userDAO.getUserIdByName(userName);
        /** 从数据库中查找userId在日期内的签到信息记录 */
        List<TimeTable> timeTableList = getTimeTableList(startDate, endDate, userId);
        /** 整理数据库中查询结果，拼装返回信息 */
        SomeOneTimeVo someOneTimeVo = new SomeOneTimeVo();
        someOneTimeVo.setUserName(userName);
        someOneTimeVo.setSingleRecordVoList(togetherData(timeTableList));
        return someOneTimeVo;
    }

    @Override
    public ResultVo getRenyiTime(String startDate, String endDate) {
        /* 用户名单 */
        List<String> userNameList = userDAO.getUserNameById();
        /* 签到有效次数 */
        int timeFre = 0;
        String totalTime = "0-0";
        List<AllTimeListVo> allTimeListVoList = new ArrayList<>();
        /* 循环用户，拼装返回信息 */
        for (String userName : userNameList) {
            /* 不显示张三 */
            if ("测试_张三".equals(userName)) {
                continue;
            }
            timeFre = 0;
            totalTime = "0-0";
            /* 由用户名获取userId */
            UserInfo userInfo = userDAO.getUserByName(userName);
            AllTimeListVo allTimeListVo = new AllTimeListVo();
            allTimeListVo.setUserName(userName);
            /* 查询一个人的在日期内的签到情况 */
            SomeOneTimeVo someOneTimeVo = getRenyiUserRecord(startDate, endDate, userInfo.getUserId(), userName);

            /* 循环遍历签到有效次数 */
            for (int i = 0; i < someOneTimeVo.getSingleRecordVoList().size(); i++) {
                if (someOneTimeVo.getSingleRecordVoList().get(i).getTimeValid().equals(VALID)) {
                    timeFre++;
                }
            }
            allTimeListVo.setTimeFre(timeFre);
            /* 计算某人所有记录的总时间 */
            totalTime = calculateTotalTime(someOneTimeVo.getSingleRecordVoList());
            allTimeListVo.setTotalTime(totalTime);
            String[] temptotal = totalTime.split("-");
            allTimeListVo.setTotalHours(Integer.parseInt(temptotal[0]));
            allTimeListVo.setTotalMinutes(Integer.parseInt(temptotal[1]));
            /* 计算某人的平均时间 */
            String averageTime = DateUtil.calculateAverageTime(totalTime, timeFre);
            String[] tempA = averageTime.split("-");
            allTimeListVo.setAHours(Integer.parseInt(tempA[0]));
            allTimeListVo.setAMinutes(Integer.parseInt(tempA[1]));
            /* 该时间段打卡是否合格 */
            allTimeListVo.setIsQualified(isQualify(userInfo.getUserId(), totalTime, averageTime));
            allTimeListVo.setUnQualifyTimes(userInfo.getUnQualifyTimes());
            allTimeListVoList.add(allTimeListVo);
        }

        return ResultVoUtil.success(allTimeListVoList);
    }

    @Override
    public ResultVo getSelfRenyi(String startDate, String endDate) {
        return null;
    }

    /**
     * 拼凑返回信息之setSingleRecordVoList
     *
     * @param timeTableList
     * @return
     */
    public List<SingleRecordVo> togetherData(List<TimeTable> timeTableList) {
        List<SingleRecordVo> singleRecordVoList = new ArrayList<>();
        /** 循环遍历 */
        for (TimeTable timeTable : timeTableList) {
            SingleRecordVo singleRecordVo = new SingleRecordVo();
            /** 日期 */
            singleRecordVo.setTimeDate(timeTable.getTimeDate());
            /** 签到时间 */
            singleRecordVo.setTimeIn(timeTable.getTimeIn());
            /** 签退时间不为空，则set */
            if (timeTable.getTimeOut() != null) {
                if (SUPP.equals(timeTable.getTimeValid())) {
                    /** 补签成功 */
                    singleRecordVo.setTimeOut("");
                    singleRecordVo.setTimeTotal(timeTable.getTimeOut());
                    singleRecordVo.setHours(Integer.valueOf(timeTable.getTimeOut()));
                    singleRecordVo.setMinutes(0);
                } else if (SUPP_NOT_PASS.equals(timeTable.getTimeValid())) {
                    /** 补签失败 */
                    singleRecordVo.setTimeOut("");
                    singleRecordVo.setTimeTotal("");
                    singleRecordVo.setHours(Integer.valueOf(timeTable.getTimeOut()));
                    singleRecordVo.setMinutes(0);
                } else if (VALID.equals(timeTable.getTimeValid())) {
                    /** 记录有效 */
                    singleRecordVo.setTimeOut(timeTable.getTimeOut());
                    /* 计算单条记录时长 */
                    String timeTotal = calculateTimeLen(timeTable.getTimeIn(), timeTable.getTimeOut());
                    singleRecordVo.setTimeTotal(timeTotal);
                    /* 获取时、分 */
                    String[] tempTime = timeTotal.split("-");
                    singleRecordVo.setHours(Integer.parseInt(tempTime[0]));
                    singleRecordVo.setMinutes(Integer.parseInt(tempTime[1]));
                } else {
                    /** 记录无效 */
                    singleRecordVo.setTimeOut(timeTable.getTimeOut());
                    singleRecordVo.setTimeTotal("0-0");
                }
                singleRecordVo.setTimeValid(timeTable.getTimeValid());
            } else {
                /** 没有签退，则该次记录时长为0 */
                singleRecordVo.setTimeTotal("0-0");
                singleRecordVo.setHours(0);
                singleRecordVo.setMinutes(0);
                singleRecordVo.setTimeValid(UNVALID);
            }
            singleRecordVoList.add(singleRecordVo);
        }
        return singleRecordVoList;
    }

    /**
     * 判断打卡时间是否合格
     *
     * @param totalTime
     * @return
     */
    public String isQualify(String userId, String totalTime, String averageTime) {
        String flag = UNVALID;
        String[] tempTotal = totalTime.split("-");
        String[] averageStr = averageTime.split("-");

        String strId = userId.substring(0, 4);
        if ("2018".equals(strId)) {
            /** 大一打卡总时间超过28小时，且平均时长小于8小时，认为合格 */
            if (Integer.parseInt(tempTotal[0]) >= FRASHMAN && Integer.parseInt(averageStr[0]) < AVERAGETIMEHOUR) {
                flag = VALID;
            }
        } else {
            /** 打卡总时间超过35小时，且平均时长小于8小时，认为合格 */
            if (Integer.parseInt(tempTotal[0]) >= PRESCRIBEDTIME && Integer.parseInt(averageStr[0]) < AVERAGETIMEHOUR) {
                flag = VALID;
            }
        }
        return flag;
    }

    /**
     * 计算记录时长
     *
     * @param dateIn
     * @param dateOut
     * @return
     */
    public String calculateTimeLen(String dateIn, String dateOut) {
        Date date1 = DateUtil.convertStringToTime(dateIn);
        Date date2 = DateUtil.convertStringToTime(dateOut);
        String timeDiff = DateUtil.getTimeDiff(date1, date2);
        return timeDiff;
    }

    /**
     * 将所有时长加和
     *
     * @param singleRecordVoList
     * @return
     */
    public String calculateTotalTime(List<SingleRecordVo> singleRecordVoList) {
        String total = "0-0";
        /* 循环将每条记录时长加起来 */
        for (int i = 0; i < singleRecordVoList.size(); i++) {
            if (SUPP.equals(singleRecordVoList.get(i).getTimeValid())) {
                total = DateUtil.calculateTotalTimeDiff(total, singleRecordVoList.get(i).getTimeTotal() + "-0");
            } else if (SUPP_NOT_PASS.equals(singleRecordVoList.get(i).getTimeValid())) {

            } else if (VALID.equals(singleRecordVoList.get(i).getTimeValid())) {
                total = DateUtil.calculateTotalTimeDiff(total, singleRecordVoList.get(i).getTimeTotal());
            }
        }
        return total;
    }

    /**
     * 判断用户所查是本周的，还是以前的
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public Boolean isAgo(String startDate, String endDate) {
        Boolean flag = false;
        DateTime s = new DateTime(startDate);
        DateTime e = new DateTime(endDate);
        Interval interval = new Interval(s, e);
        flag = interval.contains(DateTime.now());
        if (DateUtil.getTimeDate().equals(startDate)) {
            flag = true;
        } else if (DateUtil.getTimeDate().equals(endDate)) {
            flag = true;
        }
//        System.out.println("flag: " + flag);
        return flag;
    }

    /**
     * 查询日期内的签到记录
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<TimeTable> getTimeTableList(String startDate, String endDate, String userId) {
        List<TimeTable> timeTableList = null;
        if (isAgo(startDate, endDate)) {
            /* 查询出符合条件的签到记录 */
            timeTableList = timeTableDAO.getTablesByDate(startDate, endDate, userId);
        } else {
            timeTableList = timeTableDAO.getAgoTableByDate(startDate, endDate, userId);
        }
        return timeTableList;
    }


    /**
     * 查询某人任意时间
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    public SomeOneTimeVo getRenyiUserRecord(String startDate, String endDate, String userId, String userName) {
        SomeOneTimeVo someOneTimeVo = new SomeOneTimeVo();
        List<TimeTable> timeTableList = new ArrayList<>();

        //新、旧表联查
        //查询旧表
        List<TimeTable> list3 = getRecordListFromOldTable(startDate, endDate, userId);
        //查询新表
        List<TimeTable> list4 = getRecordListFromNewTable(startDate, endDate, userId);
        timeTableList.addAll(list3);
        timeTableList.addAll(list4);

        someOneTimeVo.setUserName(userName);
        someOneTimeVo.setSingleRecordVoList(togetherData(timeTableList));

        return someOneTimeVo;
    }

    /**
     * 从旧表中获取某用户某日期范围内的记录
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    public List<TimeTable> getRecordListFromOldTable(String startDate, String endDate, String userId) {
        List<TimeTable> oldTableRecordList = timeTableDAO.getAgoTableByDate(startDate, endDate, userId);
        return oldTableRecordList;
    }

    /**
     * 从新表中获取某用户某日期范围内的记录
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    public List<TimeTable> getRecordListFromNewTable(String startDate, String endDate, String userId) {
        List<TimeTable> newTableRecordList = timeTableDAO.getTablesByDate(startDate, endDate, userId);
        return newTableRecordList;
    }


}
