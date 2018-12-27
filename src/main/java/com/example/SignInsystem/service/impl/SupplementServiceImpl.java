package com.example.SignInsystem.service.impl;

import com.example.SignInsystem.dao.SupplementDAO;
import com.example.SignInsystem.dao.TimeTableDAO;
import com.example.SignInsystem.dao.UserDAO;
import com.example.SignInsystem.entity.Supplement;
import com.example.SignInsystem.entity.TimeTable;
import com.example.SignInsystem.entity.dto.SupplementDTO;
import com.example.SignInsystem.enums.BackMessageEnum;
import com.example.SignInsystem.enums.SignInExceptionEnum;
import com.example.SignInsystem.exception.SignInException;
import com.example.SignInsystem.service.SupplementService;
import com.example.SignInsystem.utils.AuthenticationUtil;
import com.example.SignInsystem.utils.DateUtil;
import com.example.SignInsystem.utils.KeyUtil;
import com.example.SignInsystem.utils.ResultVoUtil;
import com.example.SignInsystem.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SupplementServiceImpl
 * @Description TODO
 * @Author q
 * @Date 18-12-3 下午10:10
 */
@Service
@Slf4j
public class SupplementServiceImpl implements SupplementService {
    /**
     * 允许
     */
    private final int PASS = 2;
    /**
     * 拒绝
     */
    private final int NOT_PASS = 1;
    /**
     * 等待审批
     */
    private final int WAITING = 0;
    /**
     * 为0时，签到可用
     */
    private final int SIGN_IN = 0;
    /**
     * 该条记录为补签且通过
     */
    private final String SUPP = "B";
    /**
     * 该条补签拒绝
     */
    private final String SUPP_NOT_PASS = "R";


    @Autowired
    private SupplementDAO supplementDAO;
    @Autowired
    private TimeTableDAO timeTableDAO;
    @Autowired
    private UserDAO userDAO;

    public void isCorrect(String startDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        String applyDate = startDate;
        DateTime apply = DateTime.parse(applyDate, formatter);
        DateTime monday = apply.withDayOfWeek(1);
        DateTime sunday = apply.withDayOfWeek(7);
        Boolean flag = true;
        Interval interval = new Interval(monday, sunday);
        flag = interval.contains(apply);
        if (flag == false) {
            throw new SignInException(SignInExceptionEnum.SUPPLEMENT_DATE_ERROR);
        }
    }

    @Override
    public synchronized ResultVo submitSupple(SupplementDTO supplementDTO) {
        isCorrect(supplementDTO.getApplyDate());
        Supplement supplement = new Supplement();
        supplement.setSupId(KeyUtil.getUniqueKey());
        String userId = AuthenticationUtil.getAuthUserId();
        supplement.setUserId(userId);
        BeanUtils.copyProperties(supplementDTO, supplement);
        supplement.setIsPass(WAITING);
        int affectRows = supplementDAO.insertOneSupple(supplement);
        if (affectRows != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.SUPPLEMENT_SUCCESS.getMessage());
    }

    @Override
    public synchronized ResultVo passSupplement(String supId) {
        //更改补签状态为通过
        int affectRows = supplementDAO.updateOneSupple(supId, PASS);
        if (affectRows != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        //将补签记录，计入签到记录中
        Supplement supplement = supplementDAO.getOne(supId);
        TimeTable timeTable = new TimeTable();
        timeTable.setTimeId(KeyUtil.getUniqueKey());
        timeTable.setUserId(supplement.getUserId());
        timeTable.setTimeDate(supplement.getApplyDate());
        timeTable.setTimeIn("");
        timeTable.setTimeOut(supplement.getApplyTime());
        timeTable.setTimeState(SIGN_IN);
        timeTable.setTimeValid(SUPP);
        int affectRows1 = timeTableDAO.insertOneTimeSup(timeTable);
        if (affectRows1 != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.PASS_SUCCESS.getMessage());
    }

    @Override
    public synchronized ResultVo denySupplement(String supId) {
        //更改补签状态为拒绝
        int affectRows = supplementDAO.updateOneSupple(supId, NOT_PASS);
        if (affectRows != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        //将补签记录，计入签到记录中
        Supplement supplement = supplementDAO.getOne(supId);
        TimeTable timeTable = new TimeTable();
        timeTable.setTimeId(KeyUtil.getUniqueKey());
        timeTable.setUserId(supplement.getUserId());
        timeTable.setTimeDate(supplement.getApplyDate());
        timeTable.setTimeIn("");
        timeTable.setTimeOut(supplement.getApplyTime());
        timeTable.setTimeState(SIGN_IN);
        timeTable.setTimeValid(SUPP_NOT_PASS);
        int affectRows1 = timeTableDAO.insertOneTimeSup(timeTable);
        if (affectRows1 != 1) {
            throw new SignInException(SignInExceptionEnum.SQL_ERROR);
        }
        return ResultVoUtil.success(BackMessageEnum.DENY_SUCCESS.getMessage());
    }

    @Override
    public ResultVo getSupplementList() {
        //查询出等待审批的补签列表
        List<Supplement> supplementList = supplementDAO.getAllSupplementByIsPass(WAITING);
        return ResultVoUtil.success(cleanSupList(supplementList));
    }

    @Override
    public ResultVo getSelfSupList() {
        String userId = AuthenticationUtil.getAuthUserId();
        //查询出某用户的补签列表
        List<Supplement> supplementList = supplementDAO.getSomeOneSupple(userId);
        return ResultVoUtil.success(cleanSupList(supplementList));
    }

    /**
     * 整理返回信息
     *
     * @param supplementList
     * @return
     */
    private List<Map> cleanSupList(List<Supplement> supplementList) {
        List<Map> mapList = new ArrayList<>();
        supplementList.forEach(supplement -> {
            Map<String, Object> map = new HashMap<>();
            String name = userDAO.getNameById(supplement.getUserId());
            map.put("supId", supplement.getSupId());
            map.put("username", name);
            map.put("applyExcuse", supplement.getApplyExcuse());
            map.put("applyDate", supplement.getApplyDate());
            map.put("applyTime", supplement.getApplyTime());
            map.put("isPass", supplement.getIsPass());
            mapList.add(map);
        });
        return mapList;
    }


}
