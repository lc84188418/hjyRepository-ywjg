package com.hjy.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.hall.dao.THallQueueMapper;
import com.hjy.hall.dao.THallTakenumberMapper;
import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.dao.TSysBusinesstypeMapper;
import com.hjy.system.service.TSysBusinesstypeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (TSysBusinesstype)表服务实现类
 *
 * @author liuchun
 * @since 2020-07-28 16:54:27
 */
@Service
public class TSysBusinesstypeServiceImpl implements TSysBusinesstypeService {
    @Autowired
    private TSysBusinesstypeMapper tSysBusinesstypeMapper;
    @Autowired
    private THallTakenumberMapper tHallTakenumberMapper;
    @Autowired
    private THallQueueMapper tHallQueueMapper;
    /**
     * 通过ID查询单条数据
     *
     * @param pkBusinesstypeId 主键
     * @return 实例对象
     */
    @Override
    public TSysBusinesstype selectById(Object pkBusinesstypeId) throws Exception{
        return this.tSysBusinesstypeMapper.selectById(pkBusinesstypeId);
    }

    /**
     * 新增数据
     *
     * @param tSysBusinesstype 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int insert(TSysBusinesstype tSysBusinesstype) throws Exception{
        tSysBusinesstype.setPkBusinesstypeId(IDUtils.getUUID());
        return tSysBusinesstypeMapper.insertSelective(tSysBusinesstype);
    }
    @Transactional()
    @Override
    public CommonResult tSysBusinesstypeAdd(TSysBusinesstype tSysBusinesstype){
        tSysBusinesstype.setPkBusinesstypeId(IDUtils.getUUID());
        /**
         * 名称规范
         */
        String typeName = tSysBusinesstype.getTypeName();
        if(typeName != null){
            if( typeName.getBytes().length >= 60 ){
                return new CommonResult(445,"error","业务类型名超过最大值!(20位汉字或60字符)请联系维护人员或缩短名称",null);
            }
            if(typeName.contains("/")){
                return new CommonResult(446,"error","业务类型名中不可出现/字符",null);
            }
        }
        /**
         * 标识规范
         */
        String typeLevel = tSysBusinesstype.getTypeLevel();
        if(typeLevel.getBytes().length > 2){
            return new CommonResult(447,"error","业务标识过长，请重新输入，（最大值2位）",null);

        }
        //1.正则表达式
        Pattern p = Pattern.compile("[a-zA-Z]");
        //2.匹配原字符
        Matcher m =p.matcher(typeLevel);
        //3.正则表达式是否与整个字符串匹配
        if(!m.matches()){
            return new CommonResult(448,"error","业务标识中不可出现阿拉伯数字",null);
        }
        /**
         * 流水号是否为空规范
         */
        Integer whetherNull = tSysBusinesstype.getWhetherNull();
        if(whetherNull == null){
            return new CommonResult(449,"error","流水号是否允许为空未选择，请选择后重试",null);
        }
//        else{
//            if(!(whetherNull == 1 && whetherNull == 0)){
//                return new CommonResult(449,"error","流水号是否允许为空传值有误，请联系维护人员！",null);
//            }
//        }
        int i =tSysBusinesstypeMapper.insertSelective(tSysBusinesstype);
        if(i>0){
            return new CommonResult(200,"success","业务类型数据添加成功!",null);
        }else {
            return new CommonResult(444,"error","业务类型数据添加失败!",null);
        }
    }

    /**
     * 修改数据
     *
     * @param tSysBusinesstype 实例对象
     * @return 实例对象
     */
    @Transactional()
    @Override
    public int updateById(TSysBusinesstype tSysBusinesstype) throws Exception{
        return tSysBusinesstypeMapper.updateById(tSysBusinesstype);
    }

    /**
     * 通过主键删除数据
     *
     * @param pkBusinesstypeId 主键
     * @return 是否成功
     */
    @Transactional()
    @Override
    public int deleteById(String pkBusinesstypeId) throws Exception{
        return tSysBusinesstypeMapper.deleteById(pkBusinesstypeId);
    }
    
    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TSysBusinesstype> selectAll() throws Exception{
        return this.tSysBusinesstypeMapper.selectAll();
    }
    /**
     * 通过实体查询多条数据
     * @return 对象列表
     */
    @Override
    public List<TSysBusinesstype> selectAllByEntity(TSysBusinesstype tSysBusinesstype) throws Exception{
        return this.tSysBusinesstypeMapper.selectAllByEntity(tSysBusinesstype);
    }
    //查询所有业务类型名称
    @Override
    public List<String> selectBusinessName() {
        return tSysBusinesstypeMapper.selectBusinessName();
    }
    //查询所有业务类型名称+标识
    @Override
    public List<TSysBusinesstype> selectBusinessNameAndLevel() {
        return tSysBusinesstypeMapper.selectBusinessNameAndLevel();
    }
    //查询所有业务类型名称+标识
    @Override
    public List<TSysBusinesstype> selectBusinessNameAndLevel2() {
        return tSysBusinesstypeMapper.selectBusinessNameAndLevel2();
    }

    @Transactional()
    @Override
    public CommonResult tSysBusinesstypeDel(String param) {
        JSONObject jsonObject = JSON.parseObject(param);
        String idStr=String.valueOf(jsonObject.get("pk_id"));
        int i = tSysBusinesstypeMapper.deleteById(idStr);
        //删除未处理的该号码

        if(i > 0){
            return new CommonResult(200,"success","数据删除成功!",null);
        }else {
            return new CommonResult(444,"error","数据删除失败!",null);
        }
    }
}