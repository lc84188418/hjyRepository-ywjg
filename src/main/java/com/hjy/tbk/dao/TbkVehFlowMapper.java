package com.hjy.tbk.dao;


import com.hjy.tbk.entity.TbkVehFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbkVehFlowMapper {

    List<TbkVehFlow> selectByLsh(@Param("lsh") String lsh);

}
