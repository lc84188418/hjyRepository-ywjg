package com.hjy.tbk.service;

import com.hjy.tbk.entity.TbkDrvFlow;
import com.hjy.tbk.entity.TbkVehicle;

import java.util.List;

public interface TbkDrvFlowService {

    List<TbkDrvFlow> selectByLsh(String lsh);

}
