package com.hjy.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.JsonUtil;
import com.hjy.common.utils.file.MyFileUtil;
import com.hjy.common.utils.page.PageResult;
import com.hjy.common.utils.page.PageUtil;
import com.hjy.hall.dao.THallQueueMapper;
import com.hjy.hall.entity.THallQueue;
import com.hjy.list.dao.TListAgentMapper;
import com.hjy.list.dao.TListInfoMapper;
import com.hjy.list.entity.TListAgent;
import com.hjy.list.entity.TListInfo;
import com.hjy.list.service.TListInfoService;
import com.hjy.synthetical.entity.TSyntheticalRecord;
import com.hjy.synthetical.service.TSyntheticalRecordService;
import com.hjy.system.entity.ActiveUser;
import com.hjy.tbk.dao.TbkDrivinglicenseMapper;
import com.hjy.tbk.dao.TbkVehicleMapper;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.service.TbkDrivinglicenseService;
import com.hjy.tbk.service.TbkVehicleService;
import com.hjy.tbk.statusCode.HPStatus;
import com.hjy.tbk.statusCode.SYXZStatus;
import com.hjy.tbk.statusCode.VehicleStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

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
    @Autowired
    private TListAgentMapper tListAgentMapper;
    @Autowired
    private THallQueueMapper tHallQueueMapper;
    @Autowired
    private TSyntheticalRecordService tSyntheticalRecordService;
    @Autowired
    private TbkVehicleService tbkVehicleService;
    @Autowired
    private TbkDrivinglicenseService tbkDrivinglicenseService;
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
     * @param tListInfo 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public CommonResult insertList(TListInfo tListInfo, HttpSession session){
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        tListInfo.setOperator(activeUser.getFullName());
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
        //说明、其他字段较长时
        String temp1 = this.strBufferUtil(tListInfo.getExplain());
        String temp2 = this.strBufferUtil(tListInfo.getOther());
        tListInfo.setExplain(temp1);
        tListInfo.setOther(temp2);
        //先查询此人是否在黑名单或者红名单中
        TListInfo obj = tListInfoMapper.selectByIdCard(tListInfo.getIdCard());
        StringBuffer stringBuffer = new StringBuffer();
        if(obj == null){
            //新增数据
            tListInfo.setPkListId(IDUtils.getUUID());
            tListInfo.setApplyBook(sqsPath);
            tListInfo.setCodeCertificates(zzjgdmzPath);
            int i = tListInfoMapper.insertSelective(tListInfo);
            if(i > 0){
                stringBuffer.append("黑/红名单新增成功，待审批!");
            }
        }else {
            //修改数据
            tListInfo.setPkListId(obj.getPkListId());
            tListInfo.setApplyBook(sqsPath);
            tListInfo.setCodeCertificates(zzjgdmzPath);
            //清楚审批数据
            int j =  tListInfoMapper.updateById(tListInfo);
            if(j > 0){
                stringBuffer.append("名单库已存在 "+tListInfo.getFullName()+",已变更数据,待审批！");
            }
        }
        return new CommonResult(200, "success", stringBuffer.toString(), null);
    }
    @Transactional()
    @Override
    public int insert(TListInfo tListInfo){
        return tListInfoMapper.insertSelective(tListInfo);
    }

    private String strBufferUtil(String param) {
        if(StringUtils.isEmpty(param)){
            return null;
        }else {
            StringBuffer resultBuffer = new StringBuffer();
            //
            int byteLength = param.getBytes().length;
            if(byteLength >3999){
                //字节过长，只取其中一部分
                resultBuffer.append("输入字段已超过4000字节，固只录入部分");
                if(param.length() > 1300){
                    resultBuffer.append(param.substring(0,1200));
                }
                return resultBuffer.toString();
            }else {
                return param;
            }
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
     * @param tListInfo 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(TListInfo tListInfo) throws Exception{
        tListInfo.setApprovalTime(new Date());
        //说明、其他字段较长时
        String temp1 = this.strBufferUtil(tListInfo.getExplain());
        String temp2 = this.strBufferUtil(tListInfo.getOther());
        tListInfo.setExplain(temp1);
        tListInfo.setOther(temp2);
        //修改图片和路径
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

    @Transactional()
    @Override
    public CommonResult delApproval(String param, HttpSession session) {
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr=String.valueOf(jsonObject.get("pk_id"));
        TListInfo entity = tListInfoMapper.selectById(idStr);
        if(entity.getListType().equals("黑名单")){
            entity.setListType("黑名单删除");
        }else {
            entity.setListType("红名单删除");
        }
        entity.setOther(activeUser.getFullName()+"申请删除");
        entity.setWhetherPass(null);
        entity.setApprovalPeople(null);
        entity.setApprovalTime(null);
        int i= tListInfoMapper.updateById(entity);
        if(i>0){
            return new CommonResult(200,"success","已申请删除黑红名单数据成功，待审批！",null);
        }else {
            return new CommonResult(444,"error","申请删除黑红名单数据失败!",null);
        }
    }
    @Transactional()
    @Override
    public CommonResult tListInfoDel(TListInfo tListInfo,HttpSession session) {

        String idStr = tListInfo.getPkListId();
        String whetherPass = tListInfo.getWhetherPass();
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer();
        if(!StringUtils.isEmpty(whetherPass)){
            if(whetherPass.equals("通过")){
                //删除数据
                i= tListInfoMapper.deleteById(idStr);
                if(i > 0){
                    stringBuffer.append("审批通过,已将该数据移除！");
                }
            }else {
                //将数据变回原数据
                TListInfo entity = tListInfoMapper.selectById(idStr);
                String listType = "";
                if(entity.getListType().equals("黑名单删除")){
                    entity.setListType("黑名单");
                    listType = "黑名单数据还原！";
                }else {
                    entity.setListType("红名单");
                    listType = "红名单数据还原！";
                }
                entity.setOther("删除被拒绝");
                entity.setWhetherPass("通过");
                entity.setApprovalTime(new Date());
                ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
                entity.setApprovalPeople(activeUser.getFullName());
                i= tListInfoMapper.updateById(entity);
                if(i > 0){
                    stringBuffer.append("审批拒绝,已将"+listType);
                }
            }
        }
        if(i>0){
            return new CommonResult(200,"success",stringBuffer.toString(),null);
        }else {
            return new CommonResult(444,"error","删除数据-审批失败!",null);
        }
    }

    /**
     * 待审批列表
     * @return PageResult
     */
    @Override
    public PageResult selectWaitApproval(String param) throws Exception{
        JSONObject json = JSON.parseObject(param);
        String listType = "名单";
        String temp = JsonUtil.getStringParam(json,"listType");
        String fullName = JsonUtil.getStringParam(json,"fullName");
        String idCard = JsonUtil.getStringParam(json,"idCard");
        if(!StringUtils.isEmpty(temp)){
            listType = temp;
        }
        if(listType.contains("名单")){
            int total = tListInfoMapper.selectWaitApprovalSize(listType,fullName,idCard);
            PageResult result = PageUtil.getPageResult(param,total);
            //查询黑红名单的删除审批列表
            if(listType.contains("删除")){
                List<TListInfo> listInfo2 = tListInfoMapper.selectDelWaitApproval(result.getStartRow(),result.getEndRow(),listType,fullName,idCard);
                result.setContent(listInfo2);
            }else {
                //查询黑红名单的申请审批列表
                List<TListInfo> listInfo1 = tListInfoMapper.selectWaitApproval(result.getStartRow(),result.getEndRow(),listType,fullName,idCard);
                result.setContent(listInfo1);
            }
            return result;
        }else {
            //查询代办信息删除的审批列表
            //分页记录条数
            int total = tListAgentMapper.selectWaitApprovalSize();
            PageResult result = PageUtil.getPageResult(param,total);
            List<TListAgent> agentList = tListAgentMapper.selectWaitApproval(result.getStartRow(),result.getEndRow());
            result.setContent(agentList);
            return result;
        }
    }

    /**
     * 通过实体查询所有数据
     * @return PageResult
     */
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
    /**
     * 综合查询
     * @return CommonResult
     */
    @Override
    public CommonResult syntheticalSelect(THallQueue tHallQueue) {
        JSONObject resultJson = new JSONObject();
        //实体数据
        //当事人
        String bIdCard = tHallQueue.getBIdcard();
        boolean flag = true;
        if(!StringUtils.isEmpty(bIdCard)){
            String bName = "";
            while (flag){
                //3.1从排队信息去查受理人姓名
                String resultBName = tHallQueueMapper.selectBNameByIdCard(bIdCard);
                if(!StringUtils.isEmpty(resultBName)){
                    bName = resultBName;
                    break;
                }
                //3.2从排队信息去查代理人姓名
                String resultAName = tHallQueueMapper.selectANameByIdCard(bIdCard);
                if(!StringUtils.isEmpty(resultAName)){
                    bName = resultAName;
                    break;
                }
                //1从黑红名单找
                TListInfo infoB = tListInfoMapper.selectByIdCard(bIdCard);
                if(infoB != null){
                    bName = infoB.getFullName();
                    break;
                }
                //2从备案信息去找
                List<TSyntheticalRecord> tSyntheticalRecords = tSyntheticalRecordService.selectByZzjgdm(bIdCard);
                if(tSyntheticalRecords.size()>=1){
                    bName = tSyntheticalRecords.get(0).getDwMc();
                    break;
                }
                flag =false;
                break;
            }
            resultJson.put("bName",bName);
        }else {
            resultJson.put("bName",null);
        }
        //代理人
        String aIdCard = tHallQueue.getAIdcard();
        if(!StringUtils.isEmpty(aIdCard)){
            String aName = "";
            //查询代办信息
            List<THallQueue> queues = tHallQueueMapper.selectAB_Card_date(aIdCard);
            if(queues.size() > 0){
                resultJson.put("queue",queues);
                aName = queues.get(0).getAName();
            }else {
                resultJson.put("queue",null);
            }
            //从排队信息去查受理人姓名
            if(!StringUtils.isEmpty(aName)){
                String resultBName = tHallQueueMapper.selectBNameByIdCard(aIdCard);
                if(!StringUtils.isEmpty(resultBName)){
                    aName = resultBName;
                }
            }
            //查询代理人黑红名单信息
            TListInfo infoB = tListInfoMapper.selectByIdCard(aIdCard);
            if(infoB != null){
                resultJson.put("list",infoB);
                aName = infoB.getFullName();
            }else {
                resultJson.put("list",null);
            }
            resultJson.put("aName",aName);
        }else {
            resultJson.put("queue",null);
            resultJson.put("list",null);
            resultJson.put("aName",null);
        }
        return new CommonResult(200, "success", "查询成功!", resultJson);

    }
    /**
     * 综合查询后访问同步库数据
     * @return Map
     */
    @Transactional()
    @Override
    public Map<String, Object> getTbkData(THallQueue tHallQueue) {
        Map<String, Object> map = new HashMap<>();
        JSONObject resultJson = new JSONObject();
        String bIdCard = tHallQueue.getBIdcard();
//        String businessType = "机动车业务";
//        String tempBusinessType = JsonUtil.getStringParam(json,"businessType");
//        if(!StringUtils.isEmpty(tempBusinessType)){
//            businessType = tempBusinessType;
//        }
        //当事人驾驶证信息
        List<TbkDrivinglicense> brjszList = tbkDrivinglicenseService.selectByIdCard(bIdCard);
        resultJson.put("license",brjszList);
        //当事人车辆信息
        List<TbkVehicle> brclList = tbkVehicleService.selectByIdCard(bIdCard);
        //除开A的不显示
        brclList = this.getBRCLQK(brclList);
        resultJson.put("car",brclList);
        map.put("code",200);
        map.put("data",resultJson);
        map.put("msg","获取同步库数据成功！");
        return map;

    }

    //将本人车辆信息做修饰
    private List<TbkVehicle> getBRCLQK(List<TbkVehicle> brclList) {
        if(brclList == null){
            return null;
        }
        Iterator<TbkVehicle> iterator = brclList.iterator();
        while(iterator.hasNext()){
            TbkVehicle obj = iterator.next();
            if(obj.getZt().equals("A")){
                iterator.remove();
                continue;
            }
            StringBuffer cllxBuffer = new StringBuffer();
            StringBuffer ztBuffer = new StringBuffer();
            StringBuffer syxzBuffer = new StringBuffer();
//            StringBuffer tempBuffer1 = new StringBuffer();
//            //准驾车型
//            String [] zjcxstrings = obj.getZjcx().split("");
//            int i =1;
//            //翻译
//            for(String zjcx:zjcxstrings){
//                String msg = CLLXStatus.getDesc(zjcx);
//                tempBuffer1.append(i+"."+msg);
//                i++;
//            }
//            if(!StringUtils.isEmpty(cllxBuffer.toString())){
//                cllxBuffer.append("/"+tempBuffer1.toString());
//            }else {
//                cllxBuffer.append(tempBuffer1.toString());
//            }
            StringBuffer tempBuffer2 = new StringBuffer();
            //机动车状态
            String [] ztstrings = obj.getZt().split("");
            int j =1;
            //翻译
            for(String zt:ztstrings){
                String msg = VehicleStatus.getDesc(zt);
                tempBuffer2.append(j+"."+msg);
                j++;
            }
            if(!StringUtils.isEmpty(ztBuffer.toString())){
                ztBuffer.append("/"+tempBuffer2.toString());
            }else {
                ztBuffer.append(tempBuffer2.toString());
            }
            //号牌种类
            obj.setHpzl(HPStatus.getDesc(obj.getHpzl()));
            StringBuffer tempBuffer4 = new StringBuffer();
            //使用性质
            String [] syxzstrings = obj.getSyxz().split("");
            int m =1;
            //翻译
            for(String syxz:syxzstrings){
                String msg = SYXZStatus.getDesc(syxz);
                tempBuffer4.append(m+"."+msg);
                m++;
            }
            if(!StringUtils.isEmpty(syxzBuffer.toString())){
                syxzBuffer.append("/"+tempBuffer4.toString());
            }else {
                syxzBuffer.append(tempBuffer4.toString());
            }
            //---------
            obj.setExceptionReason(ztBuffer.toString());
            obj.setSyxz(syxzBuffer.toString());
        }
        return brclList;
    }

    /**
     * 通过证件号查询黑红名单信息
     * @return TListInfo
     */
    @Override
    public TListInfo selectByIdCard(String bIdcard) {
        return tListInfoMapper.selectByIdCard(bIdcard);
    }
    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TListInfo> selectAll() throws Exception{
        return this.tListInfoMapper.selectAll();
    }
    /**
     * 通过主键删除数据
     * @param pkListId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkListId) throws Exception{
        return tListInfoMapper.deleteById(pkListId);
    }

}