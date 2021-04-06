package com.hjy.common.utils.file;

import com.hjy.synthetical.entity.TSyntheticalMakecard;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

public class CardFileUtil {
    //文件夹路径
    private static final String CARD_FILE_DIR = "d:\\hjy\\ywjg\\makeCard";
    //本地makeCardfile
    private static final String CARD_FILE_PATH = "d:\\hjy\\ywjg\\makeCard\\左边.txt";
    //文件名
    private static final String CARD_FILE_NAME = "左边.txt";

    //将变动过后的制作信息发布到共享文件夹中
    public static String MakeCardShareFile(List<TSyntheticalMakecard> paramList) {
        List<String> resultList = new ArrayList<>();
        Iterator<TSyntheticalMakecard> iterator = paramList.iterator();
        while (iterator.hasNext()){
            TSyntheticalMakecard tHallMakecard = iterator.next();
            if(StringUtils.isEmpty(tHallMakecard.getBIdcard())){
                tHallMakecard.setBIdcard("XXXXXXXXXXXXXXXXXX");
                if(StringUtils.isEmpty(tHallMakecard.getBName())){
                    iterator.remove();
                }
            }else {
                if(StringUtils.isEmpty(tHallMakecard.getBName())){
                    tHallMakecard.setBName("XXXXX");
                }
            }

            //
            String msg = null;
            int bIdCardLength = tHallMakecard.getBIdcard().length();
            int aIdCardLength = 0;
            String bIdMsg = null;
            StringBuilder sb = new StringBuilder(tHallMakecard.getBIdcard());
            if(bIdCardLength >0 && bIdCardLength <10){
                bIdMsg = sb.toString();
            }else if(bIdCardLength >9) {
                bIdMsg = sb.replace(4,bIdCardLength-4,"*****").toString();
            }
            String aIdMsg = null;
            if(tHallMakecard.getAIdcard()!=null){
                aIdCardLength = tHallMakecard.getAIdcard().length();
                StringBuilder sb2 = new StringBuilder(tHallMakecard.getAIdcard());
                if(aIdCardLength >0 && aIdCardLength <10){
                    aIdMsg = sb2.toString();
                }else if(aIdCardLength >9) {
                    aIdMsg = sb2.replace(4,aIdCardLength-4,"*****").toString();
                }
            }
            if(tHallMakecard.getAIdcard() == null){
                msg = tHallMakecard.getBName()+" "+bIdMsg;
            }else {
                msg = tHallMakecard.getBName()+" "+bIdMsg+" "+aIdMsg;
            }
            resultList.add(msg+"\n");
        }
        //先清空
        try {
            FileWriter fw = new FileWriter(CARD_FILE_DIR + "\\" + CARD_FILE_NAME,false);
            fw.write("");
            // 关闭写文件,每次仅仅写一行数据，因为一个读文件中仅仅一个唯一的od
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始写入文件
        FileOutputStream out = null;
        try {
            //以追加的方式写入文件, 如果覆盖文件则这样写: out = new FileOutputStream(pathFileName), 这样就每次都会重写文件
            out = new FileOutputStream(CARD_FILE_PATH, true);
            StringBuffer bf = new StringBuffer();
            for(String s :resultList){
                bf.append(s);
            }
            String msg2 = null;
            byte[] bytes = new byte[20480];//20k,根据硬盘缓存大小调节,这个对性能很重要,可以根据服务器硬盘缓冲大小定制,以达到最佳写入速度.一般为硬盘缓存的1/4
            byte[] inbytes = null;
            inbytes = bf.toString().getBytes("utf-8");
            //临时用字节输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inbytes);
            int c;
            while ((c = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            inputStream.close();
            //结束时清空变量,养成习惯.
            bytes =null;
            inbytes =null;
            bf = null;
        }catch (Exception e){

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                out = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "成功";
    }

    //维护制证
    public static String MakeCardShareFileMaintain(List<TSyntheticalMakecard> paramList) {
        if(paramList == null){
            //开始写入文件，先清空
            try {
                FileWriter fw = new FileWriter(CARD_FILE_DIR + "\\" + CARD_FILE_NAME,false);
                fw.write("");
                // 关闭写文件,每次仅仅写一行数据，因为一个读文件中仅仅一个唯一的od
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        List<String> resultList = new ArrayList<>();
        for(TSyntheticalMakecard tHallMakecard : paramList){
            //先在首行添加制证完成的信息
            String msg = null;
            int bIdCardLength = tHallMakecard.getBIdcard().length();
            int aIdCardLength = 0;
            String bIdMsg = null;
            StringBuilder sb = new StringBuilder(tHallMakecard.getBIdcard());
            if(bIdCardLength >0 && bIdCardLength <10){
                bIdMsg = sb.toString();
            }else if(bIdCardLength >9) {
                bIdMsg = sb.replace(4,bIdCardLength-4,"*****").toString();
            }
            String aIdMsg = null;
            if(tHallMakecard.getAIdcard()!=null){
                aIdCardLength = tHallMakecard.getAIdcard().length();
                StringBuilder sb2 = new StringBuilder(tHallMakecard.getAIdcard());
                if(aIdCardLength >0 && aIdCardLength <10){
                    aIdMsg = sb2.replace(3,aIdCardLength-3,"*****").toString();
                }else if(aIdCardLength >9 && aIdCardLength <19) {
                    aIdMsg = sb2.replace(4,aIdCardLength-4,"*****").toString();
                }
            }
            if(tHallMakecard.getAIdcard() == null){
                msg = tHallMakecard.getBName()+" "+bIdMsg;
            }else {
                msg = tHallMakecard.getBName()+" "+bIdMsg+" "+aIdMsg;
            }
            resultList.add(msg+"\n");
        }
        //开始写入文件，先清空
        try {
            FileWriter fw = new FileWriter(CARD_FILE_DIR + "\\" + CARD_FILE_NAME,false);
            fw.write("");
            // 关闭写文件,每次仅仅写一行数据，因为一个读文件中仅仅一个唯一的od
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始写入文件
        FileOutputStream out = null;
        try {
            //以追加的方式写入文件, 如果覆盖文件则这样写: out = new FileOutputStream(pathFileName), 这样就每次都会重写文件
            out = new FileOutputStream(CARD_FILE_PATH, true);
            StringBuffer bf = new StringBuffer();
            for(String s :resultList){
                bf.append(s);
            }
            String msg2 = null;
            byte[] bytes = new byte[20480];//20k,根据硬盘缓存大小调节,这个对性能很重要,可以根据服务器硬盘缓冲大小定制,以达到最佳写入速度.一般为硬盘缓存的1/4
            byte[] inbytes = null;
            inbytes = bf.toString().getBytes("utf-8");
            //临时用字节输入流
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inbytes);
            int c;
            while ((c = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            inputStream.close();
            //结束时清空变量,养成习惯.
            bytes =null;
            inbytes =null;
            bf = null;
        }catch (Exception e){

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                out = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "成功";
    }
}
