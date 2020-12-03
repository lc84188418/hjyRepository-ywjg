package com.hjy.synthetical.service.impl;

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
    @Transactional()
    @Override
    public CommonResult delete(TSyntheticalMakecard tHallMakecard) {
        //数据库数据删除
        String pkCardId = tHallMakecard.getPkCardId();
        int i = tHallMakecardMapper.deleteById(pkCardId);
        StringBuffer resultBuffer = new StringBuffer();
        if(i > 0){
            resultBuffer.append("证件领取或弃用成功！");
            //异步修改共享文件，并上传
            String msg = ObjectAsyncTask.deleteMakeCardShareFile(tHallMakecard);
            resultBuffer.append(msg);
            return new CommonResult(200, "success", resultBuffer.toString(), null);
        }else {
            return new CommonResult(444, "error", "证件领取或弃用失败", null);

        }
    }
    //制作完成
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
}