package com.hjy.hall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.dao.THallJiashizhengMapper;
import com.hjy.hall.entity.THallJiashizheng;
import com.hjy.hall.entity.THallJidongche;
import com.hjy.hall.service.THallJiashizhengService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * (THallJiashizheng)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-27 14:17:48
 */
@Service
public class THallJiashizhengServiceImpl implements THallJiashizhengService {
    @Autowired
    private THallJiashizhengMapper tHallJiashizhengMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param pkJiashiId 主键
     * @return 实例对象
     */
    @Override
    public THallJiashizheng selectById(String pkJiashiId) {
        return this.tHallJiashizhengMapper.selectById(pkJiashiId);
    }

    /**
     * 新增数据
     *
     * @param tHallJiashizheng 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int insert(THallJiashizheng tHallJiashizheng) {
        return tHallJiashizhengMapper.insertSelective(tHallJiashizheng);
    }

    /**
     * 修改数据
     *
     * @param param
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(String param) {
        THallJiashizheng tHallJiashizheng = new THallJiashizheng();
        JSONObject json = JSON.parseObject(param);
        String pkJiashiId = JsonUtil.getStringParam(json,"pkJiashiId");
        tHallJiashizheng.setPkJiashiId(pkJiashiId);
        String withdrawType = JsonUtil.getStringParam(json,"withdrawType");
        tHallJiashizheng.setWithdrawType(withdrawType);
        String lack = null;
        //业务类型
        JSONArray jsonArray = json.getJSONArray("lacks");
        String lacks = jsonArray.toString();
        List<THallJiashizheng> lackList = JSONArray.parseArray(lacks, THallJiashizheng.class);
        StringBuffer lackCheck = new StringBuffer();
        if(lackList != null){
            for(THallJiashizheng obj :lackList){
                lackCheck.append(obj.getLack()+"。");
            }
        }
        if(withdrawType.equals("其他")){
            lack = JsonUtil.getStringParam(json,"otherLack");
        }else {
            lack = lackCheck.toString();
        }
        tHallJiashizheng.setLack(lack);
        String remarks = JsonUtil.getStringParam(json,"remarks");
        tHallJiashizheng.setRemarks(remarks);
        String associationNumber = JsonUtil.getStringParam(json,"associationNumber");
        tHallJiashizheng.setAssociationNumber(associationNumber);
        String fileNum = JsonUtil.getStringParam(json,"fileNum");
        tHallJiashizheng.setFileNum(fileNum);
        String drivingModel = JsonUtil.getStringParam(json,"drivingModel");
        tHallJiashizheng.setDrivingModel(drivingModel);
        return tHallJiashizhengMapper.updateById(tHallJiashizheng);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkJiashiId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkJiashiId) {
        return tHallJiashizhengMapper.deleteById(pkJiashiId);
    }



    /**
     * 通过实体查询多条数据
     *
     * @return 对象列表
     */
    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public PageResult selectAll(String param) {
        JSONObject json = JSON.parseObject(param);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deptName = JsonUtil.getStringParam(json,"deptName");
        String applicant = JsonUtil.getStringParam(json,"applicant");
        String businessType = JsonUtil.getStringParam(json,"businessType");
        String handlePeople = JsonUtil.getStringParam(json,"handlePeople");
        String withdrawType = JsonUtil.getStringParam(json,"withdrawType");
        String queryStartStr = JsonUtil.getStringParam(json,"queryStart");
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String idcard = JsonUtil.getStringParam(json,"idcard");
        String associationNumber = JsonUtil.getStringParam(json,"associationNumber");
        Date queryStart = null;
        if(queryStartStr != null && queryStartStr.length()>8){
            try {
                queryStart = ft.parse(queryStartStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String queryEndStr = JsonUtil.getStringParam(json,"queryEnd");
        Date queryEnd = null;
        if(queryEndStr != null && queryEndStr.length()>8){
            try {
                queryEnd = ft.parse(queryEndStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //实体数据
        THallJiashizheng tHallJiashizheng = new THallJiashizheng();
        tHallJiashizheng.setDeptName(deptName);
        tHallJiashizheng.setOrdinal(ordinal);
        tHallJiashizheng.setApplicant(applicant);
        tHallJiashizheng.setIdcard(idcard);
        tHallJiashizheng.setBusinessType(businessType);
        tHallJiashizheng.setHandlePeople(handlePeople);
        tHallJiashizheng.setWithdrawType(withdrawType);
        tHallJiashizheng.setStartTime(queryStart);
        tHallJiashizheng.setEndTime(queryEnd);
        tHallJiashizheng.setAssociationNumber(associationNumber);
        //分页记录条数
        int total = tHallJiashizhengMapper.selectSize(tHallJiashizheng);
        PageResult result = PageUtil.getPageResult(param,total);
        tHallJiashizheng.setStartRow(result.getStartRow());
        tHallJiashizheng.setEndRow(result.getEndRow());
        List<THallJiashizheng> list = tHallJiashizhengMapper.selectAll(tHallJiashizheng);
        result.setContent(list);
        return result;
    }

    @Override
    public THallJiashizheng selectByAssociationNumber(String associationNumber) {
        return tHallJiashizhengMapper.selectByAssociationNumber(associationNumber);
    }

    @Override
    public int selectTodayCount() {
        return tHallJiashizhengMapper.selectSizePage();
    }

    @Override
    public PageResult selectAllPage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum",1);
        jsonObject.put("pageSize",10);
        String param = jsonObject.toJSONString();
        //分页记录条数
        int total = tHallJiashizhengMapper.selectSizePage();
        PageResult result = PageUtil.getPageResult(param,total);
        List<THallJiashizheng> list = tHallJiashizhengMapper.selectAllPage(result.getStartRow(),result.getEndRow());
        result.setContent(list);
        return result;
    }
}