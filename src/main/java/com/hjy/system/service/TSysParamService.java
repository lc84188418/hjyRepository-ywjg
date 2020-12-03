package com.hjy.system.service;

import com.hjy.common.domin.CommonResult;
import com.hjy.list.entity.TListInfo;
import com.hjy.system.entity.TSysParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * (TSysParam)表服务接口
 *
 * @author liuchun
 * @since 2020-08-11 15:51:59
 */
public interface TSysParamService {


    /**
     * 修改数据
     *
     * @param tSysParam 实例对象
     * @return 实例对象
     */
    CommonResult updateById(TSysParam tSysParam, HttpSession session) throws Exception;


    /**
     * 查询所有数据
     * @return list
     */
     List<TSysParam> selectAll() throws Exception;

     TSysParam selectById(String pkParamId) throws Exception;

     String selectParamById(String pkParamId);
    //查询预警所需的参数值
    List<TSysParam> selectWarningParam();
}