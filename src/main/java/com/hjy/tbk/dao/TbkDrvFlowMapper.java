package com.hjy.tbk.dao;


import com.hjy.tbk.entity.TbkDrvFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbkDrvFlowMapper {

    List<TbkDrvFlow> selectByLsh(@Param("lsh") String lsh);

}
