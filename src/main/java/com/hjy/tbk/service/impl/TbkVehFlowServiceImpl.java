package com.hjy.tbk.service.impl;

import com.hjy.common.utils.PropertiesUtil;
import com.hjy.system.dao.TSysParamMapper;
import com.hjy.tbk.dao.TbkVehFlowMapper;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehFlow;
import com.hjy.tbk.service.TbkVehFlowService;
import com.hjy.warning.manager.TbkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuchun
 * @createDate 2020/11/25 12:16
 * @Classname TbkVehFlowServiceImpl
 * @Description TODO
 */
@Service
public class TbkVehFlowServiceImpl implements TbkVehFlowService {
    @Autowired
    private TbkVehFlowMapper tbkVehFlowMapper;
    @Autowired
    private TSysParamMapper tSysParamMapper;
    @Override
    public List<TbkVehFlow> selectByLsh(String lsh) {
        List<TbkVehFlow> resultList = new ArrayList<>();
        String turnon = PropertiesUtil.getValue("test.whether.brcl.data.info");
        if(turnon.equals("true")) {
            return TbkManager.getTbkVehFlowList(lsh);
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
            //本人机动车流水信息
            resultList = tbkVehFlowMapper.selectByLsh(lsh);
        }
        return resultList;
    }
}
