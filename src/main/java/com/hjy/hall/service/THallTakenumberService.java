package com.hjy.hall.service;

import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallTakenumber;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (THallTakenumber)表服务接口
 *
 * @author liuchun
 * @since 2020-07-29 10:28:25
 */
public interface THallTakenumberService {

    /**
     * 修改数据
     * @param tHallTakenumber 实例对象
     * @return 实例对象
     */
    int updateById(THallTakenumber tHallTakenumber);


    THallTakenumber getByOrdinal(String Ordinal);

    //主页-大厅实时等候人数统计
    int indexDataWaitNum();

    int insert(THallTakenumber tHallTakenumber);

    int insertSelective(THallTakenumber tHallTakenumber);

    String deleteByOrdinal(String vip_ordinal);
}