package com.hjy.log.service.impl;

import com.hjy.log.dao.TLogExceptionMapper;
import com.hjy.log.entity.TLogException;
import com.hjy.log.service.TLogExceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * (TLogException)表服务实现类
 *
 * @author liuchun
 * @since 2020-10-15 09:47:48
 */
@Service
public class TLogExceptionServiceImpl implements TLogExceptionService {
    @Autowired
    private TLogExceptionMapper tLogExceptionMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param pkExcId 主键
     * @return 实例对象
     */
    @Override
    public TLogException selectById(Object pkExcId) throws Exception {
        return this.tLogExceptionMapper.selectById(pkExcId);
    }

    /**
     * 新增数据
     *
     * @param tLogException 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int insert(TLogException tLogException) throws Exception {
        return tLogExceptionMapper.insertSelective(tLogException);
    }

    /**
     * 修改数据
     *
     * @param tLogException 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(TLogException tLogException) throws Exception {
        return tLogExceptionMapper.updateById(tLogException);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkExcId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(Object pkExcId) throws Exception {
        return tLogExceptionMapper.deleteById(pkExcId);
    }

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public List<TLogException> selectAll() throws Exception {
        return this.tLogExceptionMapper.selectAll();
    }

    /**
     * 通过实体查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public List<TLogException> selectAllByEntity(TLogException tLogException) throws Exception {
        return this.tLogExceptionMapper.selectAllByEntity(tLogException);
    }
}