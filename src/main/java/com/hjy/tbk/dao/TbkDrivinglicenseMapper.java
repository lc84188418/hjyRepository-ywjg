package com.hjy.tbk.dao;

import com.hjy.tbk.entity.TbkDrivinglicense;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbkDrivinglicenseMapper {

    List<TbkDrivinglicense> selectByIdCard(@Param("idCard") String idCard);

}
