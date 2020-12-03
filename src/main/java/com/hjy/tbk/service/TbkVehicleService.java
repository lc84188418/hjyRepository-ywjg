package com.hjy.tbk.service;

import com.hjy.tbk.entity.TbkVehicle;

import java.util.List;

public interface TbkVehicleService {

    List<TbkVehicle> selectByIdCard(String idCard);
}
