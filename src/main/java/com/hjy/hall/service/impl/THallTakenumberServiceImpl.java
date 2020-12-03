package com.hjy.hall.service.impl;

import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.typeTransUtil;
import com.hjy.hall.dao.THallTakenumberMapper;
import com.hjy.hall.entity.THallQueue;
import com.hjy.hall.entity.THallTakenumber;
import com.hjy.hall.service.THallQueueService;
import com.hjy.hall.service.THallTakenumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * (THallTakenumber)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-29 10:28:25
 */
@Service
public class THallTakenumberServiceImpl implements THallTakenumberService {
    @Autowired
    private THallTakenumberMapper tHallTakenumberMapper;

    /**
     * 修改数据
     * @param tHallTakenumber 实例对象
     * @return 实例对象
     */
    @Override
    public int updateById(THallTakenumber tHallTakenumber) {
        return tHallTakenumberMapper.updateById(tHallTakenumber);
    }

    /**
     * 通过排队号码获取取号表数据
     * @return 对象列表
     */
    @Override
    public THallTakenumber getByOrdinal(String Ordinal) {
        return this.tHallTakenumberMapper.getByOrdinal(Ordinal);
    }


    //主页-大厅实时等候人数统计
    @Override
    public int indexDataWaitNum() {
        return tHallTakenumberMapper.indexDataWaitNum();
    }


    @Override
    public int insert(THallTakenumber tHallTakenumber) {
        return tHallTakenumberMapper.insertSelective(tHallTakenumber);
    }

    @Override
    public int insertSelective(THallTakenumber tHallTakenumber) {
        return tHallTakenumberMapper.insertSelective(tHallTakenumber);
    }

    @Override
    public String deleteByOrdinal(String vip_ordinal) {
        int i = tHallTakenumberMapper.deleteByOrdinal(vip_ordinal);
        if(i>0){
            return "该号码删除成功";
        }else {
            return "该号码删除失败";
        }
    }
}