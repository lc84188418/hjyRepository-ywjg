package com.hjy.synthetical.service;

import com.hjy.common.domin.CommonResult;
import com.hjy.synthetical.entity.TSyntheticalMakecard;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * (TSyntheticalMakecard)表服务接口
 * @author liuchun
 * @since 2020-08-17 09:53:44
 */
public interface TSyntheticalMakecardService {

    /**
     * 通过ID查询单条数据
     * @param pkCardId 主键
     * @return 实例对象
     */
    TSyntheticalMakecard selectById(String pkCardId);


    /**
     * 新增数据
     * @param tHallMakecard 实例对象
     * @return 实例对象
     */
    int makeCardAdd(TSyntheticalMakecard tHallMakecard, HttpSession session);
    int insert(TSyntheticalMakecard tHallMakecard);

    /**
     * 修改数据
     * @param tHallMakecard 实例对象
     * @return 实例对象
     */
    int updateById(TSyntheticalMakecard tHallMakecard);

    /**
     * 通过主键删除数据
     * @param pkCardId 主键
     * @return 是否成功
     */
    int deleteById(String pkCardId);

    /**
     * 查询所有数据
     * @return list
     */
    List<TSyntheticalMakecard> selectAll();

    /**
     * 通过实体查询所有数据
     * @return list
     */
    List<TSyntheticalMakecard> selectAllByEntity(TSyntheticalMakecard tHallMakecard);
    /**
     * 3 删除数据-领取、弃用
     * @return 删除结果
     */
    CommonResult tHallMakecardDel(TSyntheticalMakecard tHallMakecard);
    /**
     * 3 批量领取-删除数据-领取、弃用
     * @return 删除结果
     */
    CommonResult tHallMakecardDelBatch(String param);
    /**
     * 制作完成
     * @param tHallMakecard 实体对象
     * @return 修改结果
     */
    CommonResult makeComplete(TSyntheticalMakecard tHallMakecard, HttpSession session);
    /**
     * 批量制作完成
     * @param param
     * @param session
     * @return
     */
    CommonResult makeCompleteBatch(String param, HttpSession session);
}