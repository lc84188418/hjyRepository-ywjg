package com.hjy.hall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.dao.THallJidongcheMapper;
import com.hjy.hall.entity.THallJiashizheng;
import com.hjy.hall.entity.THallJidongche;
import com.hjy.hall.service.THallJidongcheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * (THallJidongche)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-27 15:51:25
 */
@Service
public class THallJidongcheServiceImpl implements THallJidongcheService {
    @Autowired
    private THallJidongcheMapper tHallJidongcheMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param pkJidongcheId 主键
     * @return 实例对象
     */
    @Override
    public THallJidongche selectById(String pkJidongcheId) {
        return this.tHallJidongcheMapper.selectById(pkJidongcheId);
    }

    /**
     * 新增数据
     *
     * @param tHallJidongche 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int insert(THallJidongche tHallJidongche) {
        return tHallJidongcheMapper.insertSelective(tHallJidongche);
    }

    /**
     * 修改数据
     *
     * @param param 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(String param) {
        THallJidongche tHallJidongche = new THallJidongche();
        JSONObject json = JSON.parseObject(param);
        String pkJidongcheId = String.valueOf(json.get("pkJidongcheId"));
        tHallJidongche.setPkJidongcheId(pkJidongcheId);
        String withdrawType = JsonUtil.getStringParam(json,"withdrawType");
        tHallJidongche.setWithdrawType(withdrawType);
        String lack = null;
        //业务类型
        JSONArray jsonArray = json.getJSONArray("lacks");
        String lacks = jsonArray.toString();
        List<THallJidongche> lackList = JSONArray.parseArray(lacks, THallJidongche.class);
        StringBuffer lackCheck = new StringBuffer();
        if(lackList != null){
            for(THallJidongche obj :lackList){
                lackCheck.append(obj.getLack()+"。");
            }
        }
        String otherLack = JsonUtil.getStringParam(json,"otherLack");
        if(withdrawType.equals("其他")){
            lack = otherLack;
        }else {
            lack = lackCheck.toString();
        }
        tHallJidongche.setLack(lack);
        String numberType = JsonUtil.getStringParam(json,"numberType");
        tHallJidongche.setNumberType(numberType);
        String numberPlate = JsonUtil.getStringParam(json,"numberPlate");
        tHallJidongche.setNumberPlate(numberPlate);
        String associationNumber = JsonUtil.getStringParam(json,"associationNumber");
        tHallJidongche.setAssociationNumber(associationNumber);
        String fileNum = JsonUtil.getStringParam(json,"fileNum");
        tHallJidongche.setFileNum(fileNum);
        String identifyCode = JsonUtil.getStringParam(json,"identifyCode");
        tHallJidongche.setIdentifyCode(identifyCode);
        return tHallJidongcheMapper.updateById(tHallJidongche);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkJidongcheId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkJidongcheId) {
        return tHallJidongcheMapper.deleteById(pkJidongcheId);
    }

    /**
     * 通过实体查询多条数据
     *
     * @return 对象列表
     */
    /**
     * 分页查询
     * @return list
     */
    @Override
    public PageResult selectAll(String param) {
        JSONObject json = JSON.parseObject(param);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deptName = JsonUtil.getStringParam(json,"deptName");
        String applicant = JsonUtil.getStringParam(json,"applicant");
        String numberType = JsonUtil.getStringParam(json,"numberType");
        String numberPlate = JsonUtil.getStringParam(json,"numberPlate");
        String businessType = JsonUtil.getStringParam(json,"businessType");
        String handlePeople = JsonUtil.getStringParam(json,"handlePeople");
        String withdrawType = JsonUtil.getStringParam(json,"withdrawType");
        String ordinal = JsonUtil.getStringParam(json,"ordinal");
        String idcard = JsonUtil.getStringParam(json,"idcard");
        String associationNumber = JsonUtil.getStringParam(json,"associationNumber");
        String queryStartStr = String.valueOf(json.get("queryStart"));
        Date queryStart = null;
        if(queryStartStr.length()>8){
            try {
                queryStart = ft.parse(queryStartStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String queryEndStr = String.valueOf(json.get("queryEnd"));
        Date queryEnd = null;
        if(queryEndStr.length()>8){
            try {
                queryEnd = ft.parse(queryEndStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //实体数据
        THallJidongche tHallJidongche = new THallJidongche();
        tHallJidongche.setDeptName(deptName);
        tHallJidongche.setOrdinal(ordinal);
        tHallJidongche.setApplicant(applicant);
        tHallJidongche.setNumberPlate(numberPlate);
        tHallJidongche.setNumberType(numberType);
        tHallJidongche.setBusinessType(businessType);
        tHallJidongche.setHandlePeople(handlePeople);
        tHallJidongche.setWithdrawType(withdrawType);
        tHallJidongche.setStartTime(queryStart);
        tHallJidongche.setEndTime(queryEnd);
        tHallJidongche.setIdcard(idcard);
        tHallJidongche.setAssociationNumber(associationNumber);
        //分页记录条数
        int total = tHallJidongcheMapper.selectSize(tHallJidongche);
        PageResult result = PageUtil.getPageResult(param,total);
        tHallJidongche.setStartRow(result.getStartRow());
        tHallJidongche.setEndRow(result.getEndRow());
        List<THallJidongche> list = tHallJidongcheMapper.selectAll(tHallJidongche);
        result.setContent(list);
        return result;
    }

    @Override
    public THallJidongche selectByAssociationNumber(String param) {
        return tHallJidongcheMapper.selectByAssociationNumber(param);
    }

    @Override
    public int selectTodayCount() {
        return tHallJidongcheMapper.selectSizePage();
    }

    /**
     * 分页查询
     * @return list
     */
    @Override
    public PageResult selectAllPage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum",1);
        jsonObject.put("pageSize",10);
        String param = jsonObject.toJSONString();
        //分页记录条数
        int total = tHallJidongcheMapper.selectSizePage();
        PageResult result = PageUtil.getPageResult(param,total);
        List<THallJidongche> list = tHallJidongcheMapper.selectAllPage(result.getStartRow(),result.getEndRow());
        result.setContent(list);
        return result;
    }
}