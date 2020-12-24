package com.hjy.list.service;

import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.page.PageRequest;
import com.hjy.common.utils.page.PageResult;
import com.hjy.hall.entity.THallQueue;
import com.hjy.list.entity.TListInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * (TListInfo)表服务接口
 *
 * @author liuchun
 * @since 2020-08-05 12:45:38
 */
public interface TListInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param pkListId 主键
     * @return 实例对象
     */
    CommonResult selectById(String pkListId)throws Exception;


    /**
     * 新增数据
     * @param tListInfo 实例对象
     * @return 实例对象
     */
    int insert(TListInfo tListInfo);
    int insertFile(TListInfo tListInfo, MultipartFile[] files)throws Exception;


    /**
     * 修改数据
     *
     * @param tListInfo 实例对象
     * @return 实例对象
     */
    int updateById(TListInfo tListInfo) throws Exception;

    /**
     * 通过主键删除数据
     *
     * @param pkListId 主键
     * @return 是否成功
     */
    int deleteById(String pkListId) throws Exception;
    /**
     * 删除
     * @return list
     */
    CommonResult tListInfoDel(TListInfo tListInfo);
    /**
     * 删除审批
     * @return list
     */
    CommonResult delApproval(String param, HttpSession session);
    /**
     * 查询所有数据
     * @return list
     */
     List<TListInfo> selectAll() throws Exception;
    /**
     * 待审批列表
     * @return PageResult
     */
    PageResult selectWaitApproval(String param)throws Exception;

    /**
     * 通过实体查询所有数据
     * @return PageResult
     */
    PageResult selectAllPage(String param);

    TListInfo selectByIdCard(String bIdcard);
    //综合查询
    CommonResult syntheticalSelect(THallQueue tHallQueue);
    //综合查询后访问同步库数据
    Map<String, Object> getTbkData(THallQueue tHallQueue);
}