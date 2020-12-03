package com.hjy.tbk.service;

import com.hjy.tbk.entity.TbkDrivinglicense;

import java.sql.SQLException;
import java.util.List;

public interface TbkDrivinglicenseService {

    List<TbkDrivinglicense> selectByIdCard(String idCard);

}
