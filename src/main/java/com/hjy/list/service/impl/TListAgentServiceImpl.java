package com.hjy.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.list.entity.TListAgent;
import com.hjy.list.dao.TListAgentMapper;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListAgentService;
import com.hjy.system.entity.ActiveUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * (TListAgent)表服务实现类
 *
 * @author liuchun
 * @since 2020-08-05 12:45:38
 */
@Service
public class TListAgentServiceImpl implements TListAgentService {
    @Autowired
    private TListAgentMapper tListAgentMapper;

    /**
     * 通过ID查询单条数据
     * @param pkAgentId 主键
     * @return 实例对象
     */
    @Override
    public TListAgent selectById(String pkAgentId) throws Exception{
        return this.tListAgentMapper.selectById(pkAgentId);
    }

    /**
     * 新增数据
     * @param tListAgent 实例对象
     * @return 实例对象
     */
    @Override
    public int insert(TListAgent tListAgent){
        tListAgent.setPkAgentId(IDUtils.getUUID());
        tListAgent.setAddTime(new Date());
        return tListAgentMapper.insertSelective(tListAgent);
    }

    /**
     * 修改数据
     *
     * @param tListAgent 实例对象
     * @return 实例对象
     */
    @Override
    public int updateById(TListAgent tListAgent) throws Exception{
        return tListAgentMapper.updateById(tListAgent);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkAgentId 主键
     * @return 是否成功
     */
    @Override
    public int deleteById(String pkAgentId) throws Exception{
        return tListAgentMapper.deleteById(pkAgentId);
    }

    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TListAgent> selectAll() throws Exception{
        return this.tListAgentMapper.selectAll();
    }
    /**
     * 通过实体查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TListAgent> selectAllByEntity(TListAgent tListAgent) throws Exception{
        return this.tListAgentMapper.selectAllByEntity(tListAgent);
    }

    @Override
    public PageResult selectAllPage(String param)throws ParseException {
        JSONObject json = JSON.parseObject(param);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String businessType = JsonUtil.getStringParam(json,"businessType");
        String aName = JsonUtil.getStringParam(json,"aName");
        String bName = JsonUtil.getStringParam(json,"bName");
        String aIdCard = JsonUtil.getStringParam(json,"aIdCard");
        String bIdCard = JsonUtil.getStringParam(json,"bIdCard");
        String agent = JsonUtil.getStringParam(json,"agent");
        String queryStartStr = JsonUtil.getStringParam(json,"queryStart");
        Date queryStart = null;
        Date queryEnd = null;
        if(queryStartStr != null && queryStartStr.length()>8){
            queryStart = ft.parse(queryStartStr);
        }
        String queryEndStr = JsonUtil.getStringParam(json,"queryEnd");
        if(queryEndStr != null && queryEndStr.length()>8){
            queryEnd = ft.parse(queryEndStr);
        }
        //实体数据
        TListAgent tListAgent = new TListAgent();
        tListAgent.setBusinessType(businessType);
        tListAgent.setAName(aName);
        tListAgent.setAIdcard(aIdCard);
        tListAgent.setBName(bName);
        tListAgent.setBIdcard(bIdCard);
        tListAgent.setAgent(agent);
        tListAgent.setQueryStart(queryStart);
        tListAgent.setQueryEnd(queryEnd);
        //分页记录条数
        int total = tListAgentMapper.selectSize(tListAgent);
        PageResult result = PageUtil.getPageResult(param,total);
        tListAgent.setStartRow(result.getStartRow());
        tListAgent.setEndRow(result.getEndRow());
        List<TListAgent> agentList = tListAgentMapper.selectAllPage(tListAgent);
        result.setContent(agentList);
        return result;
    }

    @Transactional()
    @Override
    public CommonResult delApproval(String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr=String.valueOf(jsonObject.get("pk_id"));
        TListAgent entity = tListAgentMapper.selectById(idStr);
        entity.setRemarks("待删除");
        int i= tListAgentMapper.updateById(entity);
        if(i>0){
            return new CommonResult(200,"success","申请删除代办信息数据成功!",null);
        }else {
            return new CommonResult(444,"error","申请删除代办信息数据失败!",null);
        }
    }
    @Transactional()
    @Override
    public CommonResult tListAgentDel(String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr=String.valueOf(jsonObject.get("pk_id"));
        String whetherPass=String.valueOf(jsonObject.get("whetherPass"));
        int i = 0;
        if(!StringUtils.isEmpty(whetherPass)){
            if(whetherPass.equals("通过")){
                //删除数据
                i= tListAgentMapper.deleteById(idStr);
            }else {
                //将数据变回原数据
                TListAgent entity = tListAgentMapper.selectById(idStr);
                entity.setRemarks("删除被拒绝");
                i= tListAgentMapper.updateById(entity);
            }
        }
        if(i>0){
            return new CommonResult(200,"success","删除数据-审批成功!",null);
        }else {
            return new CommonResult(444,"error","删除数据-审批失败!",null);
        }
    }
}