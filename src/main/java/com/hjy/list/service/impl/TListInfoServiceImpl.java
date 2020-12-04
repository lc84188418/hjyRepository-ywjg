package com.hjy.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.file.MyFileUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.list.dao.TListInfoMapper;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * (TListInfo)表服务实现类
 *
 * @author liuchun
 * @since 2020-08-05 12:45:38
 */
@Service
public class TListInfoServiceImpl implements TListInfoService {
    @Autowired
    private TListInfoMapper tListInfoMapper;
    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.boot.application.ip}")
    private String webIp;
    /**
     * 通过ID查询单条数据
     *
     * @param param
     * @return 实例对象
     */
    @Override
    public CommonResult selectById(String param) throws Exception{
        JSONObject jsonObject = JSON.parseObject(param);
        String pkRecordId = String.valueOf(jsonObject.get("pk_id"));
        TListInfo listInfo = tListInfoMapper.selectById(pkRecordId);
        if(listInfo != null){
            //文件显示路径src
            StringBuffer zzjgdmzPath = new StringBuffer();
            StringBuffer sqsPath = new StringBuffer();
            zzjgdmzPath.append("http://"+webIp+":"+serverPort+"/img/"+listInfo.getCodeCertificates());
            sqsPath.append("http://"+webIp+":"+serverPort+"/img/"+listInfo.getApplyBook());
            listInfo.setCodeCertificates(zzjgdmzPath.toString());
            listInfo.setApplyBook(sqsPath.toString());
            return new CommonResult(200, "success", "名单数据查询成功!", listInfo);
        }else {
            return new CommonResult(444, "error", "名单数据查询失败!", null);
        }
    }

    /**
     * 新增数据
     *
     * @param tListInfo 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int insert(TListInfo tListInfo){
        tListInfo.setPkListId(IDUtils.getUUID());
        tListInfo.setCreateTime(new Date());
        String zzjgdmzPath = null;
        String sqsPath = null;
        if(tListInfo.getCodeCertificates() != null){
            zzjgdmzPath = tListInfo.getCodeCertificates().replace("http://"+webIp+":"+serverPort+"/img/","");
        }
        if(tListInfo.getApplyBook() != null){
            sqsPath = tListInfo.getApplyBook().replace("http://"+webIp+":"+serverPort+"/img/","");
        }
        //先查询此人是否在黑名单或者红名单中
        TListInfo obj = tListInfoMapper.selectByIdCard(tListInfo.getIdCard());
        if(obj ==null){
            //新增数据
            tListInfo.setPkListId(IDUtils.getUUID());
            tListInfo.setApplyBook(sqsPath);
            tListInfo.setCodeCertificates(zzjgdmzPath);
            return tListInfoMapper.insertSelective(tListInfo);
        }else {
            //修改数据
            tListInfo.setPkListId(obj.getPkListId());
            tListInfo.setApplyBook(sqsPath);
            tListInfo.setCodeCertificates(zzjgdmzPath);
            return tListInfoMapper.updateById(tListInfo);
        }
    }
    @Transactional()
    @Override
    public int insertFile(TListInfo tListInfo, MultipartFile[] files)throws Exception {
        tListInfo.setCreateTime(new Date());
        if(files!=null){
            String []strings = MyFileUtil.ListFileUtil(files,tListInfo.getIdCard());
            tListInfo.setApplyBook(strings[0]);
            tListInfo.setCodeCertificates(strings[1]);
        }else{
            tListInfo.setApplyBook(null);
            tListInfo.setCodeCertificates(null);
        }
        //先查询此人是否在黑名单或者红名单中
        TListInfo obj = tListInfoMapper.selectByIdCard(tListInfo.getIdCard());
        if(obj ==null){
            //新增数据
            tListInfo.setPkListId(IDUtils.getUUID());
            return tListInfoMapper.insertSelective(tListInfo);
        }else {
            //修改数据
            tListInfo.setPkListId(obj.getPkListId());
            return tListInfoMapper.updateById(tListInfo);
        }

    }
    /**
     * 修改数据
     *
     * @param tListInfo 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(TListInfo tListInfo) throws Exception{
        tListInfo.setApprovalTime(new Date());
        String zzjgdmzPath = null;
        String sqsPath = null;
        if(tListInfo.getCodeCertificates() != null){
            zzjgdmzPath = tListInfo.getCodeCertificates().replace("http://"+webIp+":"+serverPort+"/img/","");
            tListInfo.setCodeCertificates(zzjgdmzPath);
        }
        if(tListInfo.getApplyBook() != null){
            sqsPath = tListInfo.getApplyBook().replace("http://"+webIp+":"+serverPort+"/img/","");
            tListInfo.setApplyBook(sqsPath);
        }

        return tListInfoMapper.updateById(tListInfo);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkListId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkListId) throws Exception{
        return tListInfoMapper.deleteById(pkListId);
    }
    @Transactional()
    @Override
    public CommonResult tListInfoDel(String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr=String.valueOf(jsonObject.get("pk_id"));
        int i= tListInfoMapper.deleteById(idStr);
        if(i>0){
            return new CommonResult(200,"success","黑红名单数据删除成功!",null);
        }else {
            return new CommonResult(444,"error","黑红名单数据删除失败!",null);
        }
    }

    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TListInfo> selectAll() throws Exception{
        return this.tListInfoMapper.selectAll();
    }

    @Override
    public PageResult selectWaitApproval(String param) throws Exception{
        int total = tListInfoMapper.selectWaitApprovalSize();
        PageResult result = PageUtil.getPageResult(param,total);
        List<TListInfo> listInfos = tListInfoMapper.selectWaitApproval(result.getStartRow(),result.getEndRow());
        result.setContent(listInfos);
        return result;
    }


    @Override
    public PageResult selectAllPage(String param) {
        JSONObject json = JSON.parseObject(param);
        //实体数据
        String listType = JsonUtil.getStringParam(json,"listType");
        String fullName = JsonUtil.getStringParam(json,"fullName");
        String IdCard = JsonUtil.getStringParam(json,"idCard");
        String approvalPeople = JsonUtil.getStringParam(json,"operator");
        TListInfo listInfo = new TListInfo();
        listInfo.setListType(listType);
        listInfo.setFullName(fullName);
        listInfo.setIdCard(IdCard);
        listInfo.setApprovalPeople(approvalPeople);
        //分页记录条数
        int total = tListInfoMapper.selectSize(listInfo);
        PageResult result = PageUtil.getPageResult(param,total);
        List<TListInfo> listInfos = tListInfoMapper.selectAllPage(result.getStartRow(),result.getEndRow(),listType,fullName,IdCard,approvalPeople);
        result.setContent(listInfos);
        return result;
    }

    @Override
    public TListInfo selectByIdCard(String bIdcard) {
        return tListInfoMapper.selectByIdCard(bIdcard);
    }

}