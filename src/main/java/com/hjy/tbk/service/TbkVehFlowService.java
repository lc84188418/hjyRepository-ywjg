package com.hjy.tbk.service;

import com.hjy.tbk.entity.TbkVehFlow;

import java.util.List;

public interface TbkVehFlowService {

    List<TbkVehFlow> selectByLsh(String lsh);

}
