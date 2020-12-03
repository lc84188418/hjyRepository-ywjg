package com.hjy.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.list.entity.TListAgent;
import com.hjy.list.dao.TListAgentMapper;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListAgentService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResult selectAllPage(String param) {
        JSONObject json = JSON.parseObject(param);
        //实体数据
        String businessType = JsonUtil.getStringParam(json,"businessType");
        String aName = JsonUtil.getStringParam(json,"aName");
        String bName = JsonUtil.getStringParam(json,"bName");
        String aIdCard = JsonUtil.getStringParam(json,"aIdCard");
        String bIdCard = JsonUtil.getStringParam(json,"bIdCard");
        String agent = JsonUtil.getStringParam(json,"agent");
        TListAgent tListAgent = new TListAgent();
        tListAgent.setBusinessType(businessType);
        tListAgent.setAName(aName);
        tListAgent.setAIdcard(aIdCard);
        tListAgent.setBName(bName);
        tListAgent.setBIdcard(bIdCard);
        tListAgent.setAgent(agent);
        //分页记录条数
        int total = tListAgentMapper.selectSize(tListAgent);
        PageResult result = PageUtil.getPageResult(param,total);
        List<TListAgent> agentList = tListAgentMapper.selectAllPage(result.getStartRow(),result.getEndRow(),businessType,aName,aIdCard,bName,bIdCard,agent);
        result.setContent(agentList);
        return result;
    }
}