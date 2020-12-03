package com.hjy.tbk.service.impl;

import com.hjy.common.utils.PropertiesUtil;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.tbk.dao.TbkDrvFlowMapper;
import com.hjy.tbk.dao.TbkVehicleMapper;
import com.hjy.tbk.entity.TbkDrvFlow;
import com.hjy.tbk.entity.TbkVehFlow;
import com.hjy.tbk.service.TbkDrvFlowService;
import com.hjy.warning.manager.TbkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuchun
 * @createDate 2020/11/25 12:16
 * @Classname TbkDrvFlowServiceImpl
 * @Description TODO
 */
@Service
public class TbkDrvFlowServiceImpl implements TbkDrvFlowService {
    @Autowired
    private TbkDrvFlowMapper tbkDrvFlowMapper;
    @Autowired
    private TSysParamMapper tSysParamMapper;
    @Override
    public List<TbkDrvFlow> selectByLsh(String lsh) {
        List<TbkDrvFlow> resultList = new ArrayList<>();
        String turnon = PropertiesUtil.getValue("test.whether.brcl.data.info");
        if(turnon.equals("true")) {
            return TbkManager.getTbkDrvFlowList(lsh);
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
            resultList = null;
        }else {
            //本人驾驶证流水信息
            resultList = tbkDrvFlowMapper.selectByLsh(lsh);
        }
        return resultList;
    }
}
