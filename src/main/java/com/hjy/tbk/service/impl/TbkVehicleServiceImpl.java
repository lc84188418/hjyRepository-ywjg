package com.hjy.tbk.service.impl;

import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.PropertiesUtil;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.tbk.dao.TbkVehicleMapper;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.service.TbkVehicleService;
import com.hjy.warning.manager.TbkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TbkVehicleServiceImpl implements TbkVehicleService {

    @Autowired
    private TbkVehicleMapper tbkVehicleMapper;
    @Autowired
    private TSysParamMapper tSysParamMapper;

    @Override
    public List<TbkVehicle> selectByIdCard(String idCard) {
        String turnon = PropertiesUtil.getValue("test.whether.brcl.data.info");
        if(turnon.equals("true")) {
            return TbkManager.getTbkVehicleList(idCard);
        }
        List<TbkVehicle> tbkVehicleList = new ArrayList<>();
        /**
         * 查询是否需要获取同步库数据
         */
        //查询参数配置中加入黑名单代办次数限值，如果没有就默认为5次
        String value = tSysParamMapper.selectParamById("SFHQTBKSJ");
        String SFHQTBKSJ = "否";
        if(value != null){
            SFHQTBKSJ = value;
        }
        if (SFHQTBKSJ.equals("否")) {
            tbkVehicleList = null;
        }else {
            //本人所有车辆信息
            tbkVehicleList = tbkVehicleMapper.selectByIdCard(idCard);
        }
        //本人所有车辆信息
        return tbkVehicleList;
    }
}
