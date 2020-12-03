package com.hjy.tbk.dao;

import com.hjy.tbk.entity.TbkVehicle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbkVehicleMapper {

    List<TbkVehicle> selectByIdCard(@Param("idCard") String idCard);
}
