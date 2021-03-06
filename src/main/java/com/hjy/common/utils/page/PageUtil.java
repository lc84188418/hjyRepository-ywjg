package com.hjy.common.utils.page;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.utils.JsonUtil;

public class PageUtil {

    /**
     * 将分页信息封装到统一的接口
     * @return
     */
    public static PageResult getPageResult(String param, int total) {
        PageResult pageResult = new PageResult();
        PageRequest request = new PageRequest();
        JSONObject json = JSON.parseObject(param);
        //分页数据
        String pageNumStr = JsonUtil.getStringParam(json,"pageNum");
        String pageSizeStr = JsonUtil.getStringParam(json,"pageSize");
        if(pageNumStr == null){
            pageNumStr = "1";
        }
        if(pageSizeStr == null){
            pageSizeStr = "10";
        }
        int pageNum = Integer.parseInt(pageNumStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        if(total == 0){
            return pageResult;
        }
        //页码总数
        int pages = (int) Math.ceil(Double.valueOf(total)/Double.valueOf(pageSize));
        pageResult.setPages(pages);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        //分页查询参数
        PageRequest pageRequest = PageObjectUtils.getRequest(request);
        pageResult.setStartRow(pageRequest.getStartRow());
        pageResult.setEndRow(pageRequest.getEndRow());
        return pageResult;
    }
    public static PageResult getPageResult2(String param, int total) {
        PageResult pageResult = new PageResult();
        PageRequest request = new PageRequest();
        JSONObject json = JSON.parseObject(param);
        //分页数据
        String pageNumStr = JsonUtil.getStringParam(json,"pageNum");
        String pageSizeStr = JsonUtil.getStringParam(json,"pageSize");
        if(pageNumStr == null){
            pageNumStr = "1";
        }
        if(pageSizeStr == null){
            pageSizeStr = "15";
        }
        int pageNum = Integer.parseInt(pageNumStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        if(total == 0){
            return pageResult;
        }
        //页码总数
        int pages = (int) Math.ceil(Double.valueOf(total)/Double.valueOf(pageSize));
        pageResult.setPages(pages);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        //分页查询参数
        PageRequest pageRequest = PageObjectUtils.getRequest(request);
        pageResult.setStartRow(pageRequest.getStartRow());
        pageResult.setEndRow(pageRequest.getEndRow());
        return pageResult;
    }
}