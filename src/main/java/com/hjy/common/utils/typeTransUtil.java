package com.hjy.common.utils;

import com.hjy.system.entity.TSysBusinesstype;
import com.hjy.system.entity.TSysWindow;
import com.hjy.system.service.TSysBusinesstypeService;
import com.hjy.system.service.TSysWindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class  typeTransUtil {

    @Autowired
    private TSysBusinesstypeService tSysBusinesstypeService;

    private static typeTransUtil typeTransUtil;

    @PostConstruct
    public void init() {
        typeTransUtil= this;
        typeTransUtil.tSysBusinesstypeService= this.tSysBusinesstypeService;
    }

   public static List<String> typeTrans(String businessTypes){
       List<String> typeDealList=new ArrayList<>();
       //查询数据库所有数据类型
       List<TSysBusinesstype> typeList=typeTransUtil.tSysBusinesstypeService.selectBusinessNameAndLevel2();
       String types[]=businessTypes.split("/");//拿到当前窗口可办理的业务类型，用/分割
       for(String type : types){
          for(TSysBusinesstype businesstype: typeList){
              if( businesstype.getTypeName().equals(type)){
                  typeDealList.add(businesstype.getTypeLevel());
              }
          }
       }
        return  typeDealList;
    }
}
