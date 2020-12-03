package com.hjy.warning.manager;

import com.hjy.common.utils.DateUtil;
import com.hjy.tbk.entity.TbkDrivinglicense;
import com.hjy.tbk.entity.TbkDrvFlow;
import com.hjy.tbk.entity.TbkVehFlow;
import com.hjy.tbk.entity.TbkVehicle;
import com.hjy.tbk.statusCode.DrivingStatus;
import com.hjy.tbk.statusCode.HPStatus;
import com.hjy.tbk.statusCode.SYXZStatus;
import com.hjy.tbk.statusCode.VehicleStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TbkManager {
    public static List<TbkDrivinglicense> getTbkDrivinglicenseList(String idcard){
        List<TbkDrivinglicense> list = new ArrayList<>();
        TbkDrivinglicense obj1 = new TbkDrivinglicense();
        String status1 = "D";
        obj1.setZt(status1);
        obj1.setSfzmhm(idcard);
        obj1.setXm("刘春");
        obj1.setSjhm("18881204260");
        obj1.setJbr("经办人");
        obj1.setZjcx("M21");
        obj1.setCclzrq(new Date());
        obj1.setYxqz(DateUtil.addTime(new Date(),365));
        obj1.setSyyxqz(DateUtil.addTime(new Date(),185));
        obj1.setLsh("2080324051074");
        obj1.setDabh("510400241904");
        //预警原因
        //将所有的异常信息放入TbkVehicle对象里的exceptionReason
        StringBuffer statuStringBuffer1 = new StringBuffer();
        String [] strings = status1.split("");
        int j =1;
        //将所有的异常信息放入reason
        for(String s:strings){
            String msg = DrivingStatus.getDesc(s);
            statuStringBuffer1.append(j+"."+msg+".");
            j++;
        }
        obj1.setExceptionReason(statuStringBuffer1.toString());
        //驾驶证异常信息
        list.add(obj1);
        return list;
    }

    public static List<TbkVehicle> getTbkVehicleList(String idcard){
        List<TbkVehicle> list = new ArrayList<>();
        TbkVehicle obj1 = new TbkVehicle();
        String status1 = "Q";
        obj1.setStlb("机动车");
        obj1.setZt(status1);
        obj1.setLsh("1200918650861");
        obj1.setSfzmhm(idcard);
        obj1.setSyr("刘春");
        obj1.setHpzl("07");
        obj1.setHphm("DA3751");
        String syxz = "F";
        obj1.setSyxz(syxz);
        obj1.setClpp1("豪爵牌");
        obj1.setCllx("M21");
        obj1.setClxh("HJ150-2");
        obj1.setClsbdh("LC546131SF1A23D");
        obj1.setDabh("510400241904");
        obj1.setGlbm("车管所");
        //预警原因
        //将所有的异常信息放入TbkVehicle对象里的exceptionReason
        StringBuffer statuStringBuffer1 = new StringBuffer();
        String [] strings = status1.split("");
        int j =1;
        //将所有的异常信息放入reason
        for(String s:strings){
            String msg = VehicleStatus.getDesc(s);
            statuStringBuffer1.append(j+"."+msg+".");
            j++;
        }
        obj1.setExceptionReason(statuStringBuffer1.toString());
        //机动车异常信息
        list.add(obj1);
        TbkVehicle obj2 = new TbkVehicle();
        String status2 = "G";
        obj2.setStlb("机动车");
        obj2.setZt(status2);
        obj2.setLsh("2080108031342");
        obj2.setSfzmhm(idcard);
        obj2.setSyr("刘春");
        obj2.setHpzl("08");
        obj2.setHphm("DA3752");
        String syxz2 = "E";
        obj2.setSyxz(syxz2);
        obj2.setClpp1("钱江牌");
        obj2.setCllx("M21");
        obj2.setClsbdh("HJ150-1");
        obj2.setDabh("510400241904");
        obj2.setGlbm("车管所");
        //预警原因
        //将所有的异常信息放入TbkVehicle对象里的exceptionReason
        StringBuffer statuStringBuffer2 = new StringBuffer();
        String [] strings2 = status2.split("");
        int k =1;
        //将所有的异常信息放入reason
        for(String s:strings2){
            String msg = VehicleStatus.getDesc(s);
            statuStringBuffer2.append(k+"."+msg+".");
            k++;
        }
        obj2.setExceptionReason(statuStringBuffer2.toString());
        list.add(obj2);
        //机动车异常信息
        TbkVehicle obj3 = new TbkVehicle();
        String status3 = "MO";
        obj3.setStlb("机动车");
        obj3.setZt(status3);
        obj3.setLsh("2080108031342");
        obj3.setSfzmhm(idcard);
        obj3.setSyr("刘春");
        obj3.setHpzl("07");
        obj3.setHphm("DA3753");
        String syxz3 = "E";
        obj3.setSyxz(syxz3);
        obj3.setClpp1("钱江牌");
        obj3.setCllx("M21");
        obj3.setClsbdh("HJ150-1");
        obj3.setDabh("510400241904");
        obj3.setGlbm("车管所");
        //预警原因
        //将所有的异常信息放入TbkVehicle对象里的exceptionReason
        StringBuffer statuStringBuffer3 = new StringBuffer();
        String [] strings3 = status3.split("");
        int m =1;
        //将所有的异常信息放入reason
        for(String s:strings3){
            String msg = VehicleStatus.getDesc(s);
            statuStringBuffer3.append(m+"."+msg+".");
            m++;
        }
        obj3.setExceptionReason(statuStringBuffer3.toString());
        //机动车异常信息
        list.add(obj3);
        TbkVehicle obj4 = new TbkVehicle();
        String status4 = "ME";
        obj4.setStlb("机动车");
        obj4.setZt(status4);
        obj4.setLsh("2080108031344");
        obj4.setSfzmhm(idcard);
        obj4.setSyr("刘春");
        obj4.setHpzl("08");
        obj4.setHphm("DA3754");
        String syxz4 = "E";
        obj4.setSyxz(syxz4);
        obj4.setClpp1("钱江牌");
        obj4.setCllx("M21");
        obj4.setClsbdh("HJ150-1");
        obj4.setDabh("510400241904");
        obj4.setGlbm("车管所");
        //预警原因
        //将所有的异常信息放入TbkVehicle对象里的exceptionReason
        StringBuffer statuStringBuffer4 = new StringBuffer();
        String [] strings4 = status4.split("");
        int int4 =1;
        //将所有的异常信息放入reason
        for(String s:strings4){
            String msg = VehicleStatus.getDesc(s);
            statuStringBuffer4.append(int4+"."+msg+".");
            int4++;
        }
        obj4.setExceptionReason(statuStringBuffer4.toString());
        list.add(obj4);
        return list;
    }
    //测试数据
    public static List<TbkVehFlow> getTbkVehFlowList(String lsh) {
        List<TbkVehFlow> list = new ArrayList<>();
        TbkVehFlow obj1 = new TbkVehFlow();
        obj1.setLsh(lsh);
        obj1.setYwlx("A");
        obj1.setYwyy("B");
        obj1.setSyr("马飞");
        obj1.setHpzl("07");
        obj1.setHphm("DA3751");
        obj1.setClpp1("豪爵牌");
        obj1.setCllx("M21");
        obj1.setClxh("HJ150-2");
        obj1.setClsbdh("LC546131SF1A23D");
        obj1.setGlbm("510400000400");
        //驾驶证异常信息
//        list.add(obj1);
        return list;
//        return null;
    }

    public static List<TbkDrvFlow> getTbkDrvFlowList(String lsh) {
        List<TbkDrvFlow> list = new ArrayList<>();
        TbkDrvFlow obj1 = new TbkDrvFlow();
        obj1.setLsh(lsh);
        obj1.setSfzmhm("511522199702195779");
        obj1.setDabh("510400147471");
        obj1.setXm("马飞");
        obj1.setYwlx("A");
        obj1.setYwyy("B");
        obj1.setZjcx("A2");
        obj1.setYwzt("A");
        obj1.setGlbm("510400000400");
        //驾驶证异常信息
//        list.add(obj1);
        return list;
    }
}
