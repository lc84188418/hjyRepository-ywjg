package com.hjy.synthetical.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.file.MyFileUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.entity.THallQueue;
import com.hjy.synthetical.dao.TSyntheticalRecordMapper;
import com.hjy.synthetical.entity.TSyntheticalRecord;
import com.hjy.synthetical.service.TSyntheticalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * (TSyntheticalRecord)表服务实现类
 *
 * @author liuchun
 * @since 2020-10-30 17:44:37
 */
@Service
public class TSyntheticalRecordServiceImpl implements TSyntheticalRecordService {
    @Autowired
    private TSyntheticalRecordMapper tSyntheticalRecordMapper;
    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.boot.application.ip}")
    private String webIp;

    /**
     * 通过ID查询单条数据
     *
     * @param param 主键
     * @return 实例对象
     */
    @Override
    public CommonResult selectById(String param) throws Exception {
        JSONObject jsonObject = JSON.parseObject(param);
        String pkRecordId = String.valueOf(jsonObject.get("pk_id"));
        TSyntheticalRecord tSyntheticalRecord = tSyntheticalRecordMapper.selectById(pkRecordId);
        if(tSyntheticalRecord != null){
            //文件显示路径src
            StringBuffer zzjgdmzPath = new StringBuffer();
            StringBuffer wtsPath = new StringBuffer();
            zzjgdmzPath.append("http://"+webIp+":"+serverPort+"/img/"+tSyntheticalRecord.getZzjgdmz());
            wtsPath.append("http://"+webIp+":"+serverPort+"/img/"+tSyntheticalRecord.getWts());
            tSyntheticalRecord.setZzjgdmz(zzjgdmzPath.toString());
            tSyntheticalRecord.setWts(wtsPath.toString());
            return new CommonResult(200, "success", "备案数据查询成功!", tSyntheticalRecord);
        }else {
            return new CommonResult(444, "error", "备案数据查询失败!", null);
        }
    }
    @Override
    public CommonResult hallqueuegetRecord(String param) throws Exception {
        JSONObject jsonObject = JSON.parseObject(param);
        String zzjgdm = String.valueOf(jsonObject.get("zzjgdm"));
        List<TSyntheticalRecord> list = tSyntheticalRecordMapper.selectByZzjgdm(zzjgdm);
        //文件显示路径src
        if(list.size()>=1){
            TSyntheticalRecord tSyntheticalRecord = list.get(0);
            //文件显示路径src
            StringBuffer zzjgdmzPath = new StringBuffer();
            StringBuffer wtsPath = new StringBuffer();
            zzjgdmzPath.append("http://"+webIp+":"+serverPort+"/img/"+tSyntheticalRecord.getZzjgdmz());
            wtsPath.append("http://"+webIp+":"+serverPort+"/img/"+tSyntheticalRecord.getWts());
            tSyntheticalRecord.setZzjgdmz(zzjgdmzPath.toString());
            tSyntheticalRecord.setWts(wtsPath.toString());
            return new CommonResult(200, "success", "备案数据查询成功!", tSyntheticalRecord);
        }else {
            return new CommonResult(201, "success", "改单位暂无备案数据!请告知添加", null);
        }
    }

    /**
     * 新增数据
     *
     * @param tSyntheticalRecord 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public CommonResult insert(TSyntheticalRecord tSyntheticalRecord) throws Exception {
        tSyntheticalRecord.setPkRecordId(IDUtils.getUUID());
        tSyntheticalRecord.setBaDate(new Date());
        //对之前文件上传时返给前端的路径做处理，然后存数据库
        //http://192.168.0.127:8082/img/zzjgdm_2020_11_05_1604544097241.jpg
        //http://192.168.0.127:8082/img/wts_2020_11_05_1604544174345.jpg
        String zzjgdmzPath = null;
        String wtsPath = null;
        String by1Path = null;
        String by2Path = null;
        if(tSyntheticalRecord.getZzjgdmz() != null){
            zzjgdmzPath = tSyntheticalRecord.getZzjgdmz().replace("http://"+webIp+":"+serverPort+"/img/","");
        }
        if(tSyntheticalRecord.getWts() != null){
            wtsPath = tSyntheticalRecord.getWts().replace("http://"+webIp+":"+serverPort+"/img/","");
        }
        if(tSyntheticalRecord.getBy1Path() != null){
            by1Path = tSyntheticalRecord.getBy1Path().replace("http://"+webIp+":"+serverPort+"/img/","");
        }
        if(tSyntheticalRecord.getBy2Path() != null){
            by2Path = tSyntheticalRecord.getBy2Path().replace("http://"+webIp+":"+serverPort+"/img/","");
        }
        tSyntheticalRecord.setZzjgdmz(zzjgdmzPath);
        tSyntheticalRecord.setWts(wtsPath);
        tSyntheticalRecord.setBy1Path(by1Path);
        tSyntheticalRecord.setBy2Path(by2Path);
        int i = tSyntheticalRecordMapper.insertSelective(tSyntheticalRecord);
        if (i > 0) {
            return new CommonResult(200, "success", "添加数据成功!", null);
        }else {
            return new CommonResult(444, "error", "添加数据失败!", null);
        }
    }
    @Transactional()
    @Override
    public CommonResult tSyntheticalRecordAdd(TSyntheticalRecord tSyntheticalRecord, MultipartFile[] files){
        tSyntheticalRecord.setPkRecordId(IDUtils.getUUID());
        tSyntheticalRecord.setBaDate(new Date());
        if(files!=null){
            String []strings = MyFileUtil.BAFileUtil(files,tSyntheticalRecord.getZzjgdm());
            tSyntheticalRecord.setZzjgdmz(strings[0]);
            tSyntheticalRecord.setWts(strings[1]);
        }else{
            tSyntheticalRecord.setZzjgdmz(null);
            tSyntheticalRecord.setWts(null);
        }
        int i = tSyntheticalRecordMapper.insertSelective(tSyntheticalRecord);
        if (i > 0) {
            return new CommonResult(200, "success", "备案信息添加成功!", null);
        }else {
            return new CommonResult(444, "error", "备案信息添加失败!", null);
        }
    }

    /**
     * 修改数据
     *
     * @param tSyntheticalRecord 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public CommonResult updateById(TSyntheticalRecord tSyntheticalRecord) throws Exception {
        int i = tSyntheticalRecordMapper.updateById(tSyntheticalRecord);
        if(i > 0){
            return new CommonResult(200, "success", "备案信息修改成功!", null);
        }else {
            return new CommonResult(444, "error", "备案信息修改失败!", null);

        }
    }

    /**
     * 通过主键删除数据
     *
     * @param param 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public CommonResult deleteById(String param) throws Exception {
        JSONObject jsonObject = JSON.parseObject(param);
        String pkRecordId = String.valueOf(jsonObject.get("pk_id"));
        //删除备案的图片文件
        String msg = this.deleteRecordFile(pkRecordId);
        //删除数据库记录
        int i = tSyntheticalRecordMapper.deleteById(pkRecordId);
        if (i > 0) {
            return new CommonResult(200, "success", msg+"备案信息数据删除成功!", null);
        } else {
            return new CommonResult(444, "error", msg+"备案信息数据删除失败!", null);
        }
    }

    private String deleteRecordFile(String pkRecordId) {
        TSyntheticalRecord deleteEntity = tSyntheticalRecordMapper.selectById(pkRecordId);
        //D:/hjy/ywjg/ba/zzjgdm/
        String zzjgdmz = deleteEntity.getZzjgdmz();
        //D:/hjy/ywjg/ba/wts/
        String wts = deleteEntity.getWts();
        //D:/hjy/ywjg/ba/by1/
        String by1 = deleteEntity.getBy1Path();
        //D:/hjy/ywjg/ba/by2/
        String by2 = deleteEntity.getBy2Path();
        return "备案文件删除成功！";
    }

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public CommonResult tSyntheticalRecordList(String param){
        JSONObject json = JSON.parseObject(param);
        String balx = JsonUtil.getStringParam(json,"balx");
        String jglx = JsonUtil.getStringParam(json,"jglx");
        String zzjgdm = JsonUtil.getStringParam(json,"zzjgdm");
        String dwMc = JsonUtil.getStringParam(json,"dwMc");
        //实体数据
        TSyntheticalRecord entity = new TSyntheticalRecord();
        entity.setBalx(balx);
        entity.setJglx(jglx);
        entity.setZzjgdm(zzjgdm);
        entity.setDwMc(dwMc);
        //分页记录条数
        int total = tSyntheticalRecordMapper.selectSize(entity);
        PageResult result = PageUtil.getPageResult(param,total);
        entity.setStartRow(result.getStartRow());
        entity.setEndRow(result.getEndRow());
        List<TSyntheticalRecord> list = tSyntheticalRecordMapper.selectAllPage(entity);
        result.setContent(list);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PageResult",result);
        return new CommonResult(200, "success", "备案数据获取成功!", jsonObject);

    }

    //通过组织机构代码去查备案信息
    @Override
    public List<TSyntheticalRecord> selectByZzjgdm(String zzjgdm) {
        return tSyntheticalRecordMapper.selectByZzjgdm(zzjgdm);
    }

}