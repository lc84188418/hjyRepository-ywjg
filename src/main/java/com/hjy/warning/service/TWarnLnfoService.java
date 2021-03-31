package com.hjy.warning.service;

import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.page.PageResult;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.warning.entity.Warning;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * (TWarnLnfo)表服务接口
 *
 * @author liuchun
 * @since 2020-09-22 18:22:31
 */
public interface TWarnLnfoService {

    Warning selectByWindow(String handleStatus);

    void deleteById(Warning warning);

    int update(Warning warning);

    void insert(Warning warning);

    PageResult selectAllPage(String param) throws ParseException;

    Map<String, Object> warningCheck(String param, HttpSession session);

    CommonResult details(String param);


    JSONObject selectGaozhidan(String param);

    Warning warning12_2(Warning warning, List<TbkVehicle> brclList, List<TbkDrivinglicense> brjszList);

    Map<String, Object> getTbkData(String param, HttpServletRequest request)throws SQLException;

    CommonResult getListInfo(String param);

    void systemMaintain();
}