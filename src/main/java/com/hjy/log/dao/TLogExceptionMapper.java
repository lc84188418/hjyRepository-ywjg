package com.hjy.log.dao;

import com.hjy.log.entity.TLogException;

import java.util.List;

public interface TLogExceptionMapper {
    TLogException selectById(Object pkExcId);

    int insertSelective(TLogException tLogException);

    int updateById(TLogException tLogException);

    int deleteById(Object pkExcId);

    List<TLogException> selectAll();

    List<TLogException> selectAllByEntity(TLogException tLogException);
}
