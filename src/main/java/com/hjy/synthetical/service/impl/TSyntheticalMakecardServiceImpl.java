package com.hjy.synthetical.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.task.ObjectAsyncTask;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.PropertiesUtil;
import com.hjy.common.utils.file.CardFileUtil;
import com.hjy.common.utils.file.SmbFileUtil;
import com.hjy.synthetical.dao.TSyntheticalMakecardMapper;
import com.hjy.synthetical.entity.TSyntheticalMakecard;
import com.hjy.synthetical.service.TSyntheticalMakecardService;
import com.hjy.system.entity.ActiveUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (TSyntheticalMakecard)表服务实现类
 *
 * @author liuchun
 * @since 2020-08-17 09:53:44
 */
@Service
public class TSyntheticalMakecardServiceImpl implements TSyntheticalMakecardService {
    @Autowired
    private TSyntheticalMakecardMapper tHallMakecardMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param pkCardId 主键
     * @return 实例对象
     */
    @Override
    public TSyntheticalMakecard selectById(String pkCardId) {
        return this.tHallMakecardMapper.selectById(pkCardId);
    }

    /**
     * 新增数据
     *
     * @param tHallMakecard 实例对象
     * @return 实例对象
     */
    @Override
    public int makeCardAdd(TSyntheticalMakecard tHallMakecard, HttpSession session) {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        tHallMakecard.setPkCardId(IDUtils.getUUID());
        tHallMakecard.setCreateTime(new Date());
        tHallMakecard.setStartTime(new Date());
        tHallMakecard.setOperatorPeople(activeUser.getFullName());
        tHallMakecard.setStatus("制作中");
        int i= 0;
        if(tHallMakecard.getBName() == null ||tHallMakecard.getBIdcard() == null){
            i = 2;
        }else if( StringUtils.isEmpty(tHallMakecard.getBName()) || StringUtils.isEmpty(tHallMakecard.getBIdcard()) ){
            i = 2;

        }else{
            i = tHallMakecardMapper.insertSelective(tHallMakecard);
        }
        return i;
    }
    @Override
    public int insert(TSyntheticalMakecard tHallMakecard) {
        return tHallMakecardMapper.insertSelective(tHallMakecard);
    }

    /**
     * 修改数据
     *
     * @param tHallMakecard 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(TSyntheticalMakecard tHallMakecard) {
        //修改制证
        int i = tHallMakecardMapper.updateById(tHallMakecard);
        //制证完成
        if(i>0 && tHallMakecard.getStatus().equals("已完成")){
            //异步处理-修改制证共享文件夹
            ObjectAsyncTask.updateMakeCardShareFile(tHallMakecard);
        }
        return i;
    }

    /**
     * 通过主键删除数据
     *
     * @param pkCardId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkCardId) {
        return tHallMakecardMapper.deleteById(pkCardId);
    }

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public List<TSyntheticalMakecard> selectAll() {
        List<TSyntheticalMakecard> list = tHallMakecardMapper.selectAll();
        List<TSyntheticalMakecard> resultList = new ArrayList<>();
        List<TSyntheticalMakecard> resultList2 = new ArrayList<>();
        if(list != null){
            for(TSyntheticalMakecard obj:list){
                if(obj.getStatus().equals("已完成")){
                    resultList.add(obj);
                }else {
                    resultList2.add(obj);
                }
            }
        }else {
            return null;
        }
        resultList.addAll(resultList2);
        return resultList;
    }

    /**
     * 通过实体查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public List<TSyntheticalMakecard> selectAllByEntity(TSyntheticalMakecard tHallMakecard) {
        List<TSyntheticalMakecard> list = tHallMakecardMapper.selectAllByEntity(tHallMakecard);
        List<TSyntheticalMakecard> resultList = new ArrayList<>();
        List<TSyntheticalMakecard> resultList2 = new ArrayList<>();
        if(list != null){
            for(TSyntheticalMakecard obj:list){
                if(obj.getStatus().equals("已完成")){
                    resultList.add(obj);
                }else {
                    resultList2.add(obj);
                }
            }
        }else {
            return null;
        }
        resultList.addAll(resultList2);
        return resultList;
    }
    /**
     * 3 删除数据-领取、弃用
     * @return 删除结果
     */
    @Transactional()
    @Override
    public CommonResult tHallMakecardDel(TSyntheticalMakecard tHallMakecard) {
        //数据库数据删除
        String pkCardId = tHallMakecard.getPkCardId();
        int i = tHallMakecardMapper.deleteById(pkCardId);
        StringBuffer resultBuffer = new StringBuffer();
        if(i > 0){
            resultBuffer.append("证件领取或弃用成功！");
            //修改本地制证文件
            String msg = CardFileUtil.MakeCardShareFileDel(tHallMakecard);
            resultBuffer.append(msg);
            //将本地文件上传至共享文件
            String whether = PropertiesUtil.getValue("test.whether.update.share.file");
            if(whether.equals("true")){
                //本地文件添加完成后上传到共享文件
                String shareDir = PropertiesUtil.getValue("share.file.directory");
                String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
                try {
                    SmbFileUtil.smbPut(shareDir,localFilePath);
                    resultBuffer.append("共享文件上传成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    resultBuffer.append("共享文件上传失败");
                    throw new RuntimeException();
                }
            }
            return new CommonResult(200, "success", resultBuffer.toString(), null);
        }else {
            return new CommonResult(444, "error", "证件领取或弃用失败", null);

        }
    }

    /**
     * 3 批量领取-删除数据-领取、弃用
     * @return 删除结果
     */
    @Transactional()
    @Override
    public CommonResult tHallMakecardDelBatch(String param) {
        JSONObject json = JSON.parseObject(param);
        JSONArray jsonArray = json.getJSONArray("ids");
        String permsIdsStr = jsonArray.toString();
        List<String> idList = JSONArray.parseArray(permsIdsStr,String.class);
        if(idList != null){
            System.err.println("批量删除时idlist"+idList);
            List<TSyntheticalMakecard> list = tHallMakecardMapper.selectAllById(idList);
            //批量删除数据库数据
            int i = tHallMakecardMapper.deleteByIdList(idList);
//            int i = idList.size();
            StringBuffer resultBuffer = new StringBuffer();
            if(i > 0){
                resultBuffer.append("证件领取或弃用成功！");
                //修改本地制证文件
                String msg = CardFileUtil.MakeCardShareFileDelBatch(list);
                resultBuffer.append(msg);
                //将本地文件上传至共享文件
                String whether = PropertiesUtil.getValue("test.whether.update.share.file");
                if(whether.equals("true")){
                    //本地文件添加完成后上传到共享文件
                    String shareDir = PropertiesUtil.getValue("share.file.directory");
                    String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
                    try {
                        SmbFileUtil.smbPut(shareDir,localFilePath);
                        resultBuffer.append("共享文件上传成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        resultBuffer.append("共享文件上传失败");
                        throw new RuntimeException();
                    }
                }
                return new CommonResult(200, "success", resultBuffer.toString(), null);
            }else {
                return new CommonResult(444, "error", "证件领取或弃用失败", null);
            }
        }else {
            return new CommonResult(445, "error", "你还未选择!", null);

        }
    }

    /**
     * 制作完成
     * @param tHallMakecard 实体对象
     * @return 修改结果
     */
    @Transactional()
    @Override
    public synchronized CommonResult makeComplete(TSyntheticalMakecard tHallMakecard, HttpSession session) {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        tHallMakecard.setEndTime(new Date());
        tHallMakecard.setStatus("已完成");
        tHallMakecard.setOperatorPeople(activeUser.getFullName());
        int i = tHallMakecardMapper.updateById(tHallMakecard);
        //制证完成
        if(i>0){
            //修改制证共享文件夹
            String flag = CardFileUtil.MakeCardShareFileComplet(tHallMakecard);
            if(flag != null){
                String whether = PropertiesUtil.getValue("test.whether.update.share.file");
                if(whether.equals("true")){
                    //本地文件添加完成后上传到共享文件
                    String shareDir = PropertiesUtil.getValue("share.file.directory");
                    String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
                    try {
                        SmbFileUtil.smbPut(shareDir,localFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return new CommonResult(200, "success", "制作证件已完成!", null);
        }else {
            return new CommonResult(444, "error", "制作完成已失败!", null);
        }
    }
    //批量制作完成
    @Transactional()
    @Override
    public synchronized CommonResult makeCompleteBatch(String param, HttpSession session) {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        JSONObject json = JSON.parseObject(param);
        JSONArray jsonArray = json.getJSONArray("ids");
        String permsIdsStr = jsonArray.toString();
        List<String> idList = JSONArray.parseArray(permsIdsStr,String.class);
        System.err.println("idList:"+idList);
        if(idList != null){
            int i = tHallMakecardMapper.makeCompleteBatchUpdate(idList,activeUser.getFullName(),new Date());
            //制证完成
            if(i>0){
                //查询出选中的制证数据列表
                List<TSyntheticalMakecard> list = tHallMakecardMapper.selectAllById(idList);
                //修改制证共享文件夹
                String flag = CardFileUtil.MakeCardShareFileCompletBatch(list);
                if(flag != null){
                    String whether = PropertiesUtil.getValue("test.whether.update.share.file");
                    if(whether.equals("true")){
                        //本地文件添加完成后上传到共享文件
                        String shareDir = PropertiesUtil.getValue("share.file.directory");
                        String localFilePath = "d://hjy//ywjg//makeCard//左边.txt";
                        try {
                            SmbFileUtil.smbPut(shareDir,localFilePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return new CommonResult(200, "success", "制作证件已完成!", null);
            }else {
                return new CommonResult(444, "error", "制作完成已失败!", null);
            }
        }
        return new CommonResult(445, "error", "你还未选择!", null);
    }
}