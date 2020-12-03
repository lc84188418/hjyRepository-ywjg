package com.hjy.tbk.service.impl;

import com.hjy.common.utils.PropertiesUtil;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.tbk.dao.TbkDrivinglicenseMapper;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.service.TbkDrivinglicenseService;
import com.hjy.warning.manager.TbkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TbkDrivinglicenseImpl implements TbkDrivinglicenseService {
    @Autowired
    private TbkDrivinglicenseMapper tbkDrivinglicenseMapper;
    @Autowired
    private TSysParamMapper tSysParamMapper;
    @Override
    public List<TbkDrivinglicense> selectByIdCard(String idCard) {
        List<TbkDrivinglicense> drivinglicenseList = new ArrayList<>();
        String turnon = PropertiesUtil.getValue("test.whether.brcl.data.info");
        if(turnon.equals("true")) {
            return TbkManager.getTbkDrivinglicenseList(idCard);
        }
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
            drivinglicenseList = null;
        }else {
            //本人驾驶证信息
            drivinglicenseList = tbkDrivinglicenseMapper.selectByIdCard(idCard);
        }
        return drivinglicenseList;
    }
}
