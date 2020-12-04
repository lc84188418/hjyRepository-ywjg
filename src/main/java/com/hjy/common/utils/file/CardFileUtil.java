package com.hjy.common.utils.file;

import com.hjy.synthetical.entity.TSyntheticalMakecard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CardFileUtil {
    //文件夹路径
    private static final String CARD_FILE_DIR = "d:\\hjy\\ywjg\\makeCard";
    //本地makeCardfile
    private static final String CARD_FILE_PATH = "d:\\hjy\\ywjg\\makeCard\\左边.txt";
    //文件名
    private static final String CARD_FILE_NAME = "左边.txt";

    //制证完成添加
    public static String MakeCardShareFileComplet(TSyntheticalMakecard tHallMakecard) {
        if(tHallMakecard.getBIdcard() == null){
            return null;
        }
        List<String> resultList = new ArrayList<>();
        //先在首行添加制证完成的信息
        String msg = null;
        int bIdCardLength = tHallMakecard.getBIdcard().length();
        int aIdCardLength = 0;
        String bIdMsg = null;
        StringBuilder sb = new StringBuilder(tHallMakecard.getBIdcard());
        if(bIdCardLength >0 && bIdCardLength <10){
            bIdMsg = sb.replace(3,bIdCardLength-3,"*****").toString();
        }else if(bIdCardLength >9 && bIdCardLength <19) {
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
        //再读取文件
        FileInputStream in = null;
        try {
            in = new FileInputStream(CARD_FILE_PATH);
            //以指定的编码读取文件
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String s = null;
            while ((s = bufReader.readLine())!=null){
                resultList.add(s+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    //制证领取、弃用
    public static String MakeCardShareFileDel(TSyntheticalMakecard tHallMakecard) {
        List<String> resultList = new ArrayList<>();
        String delmsg = null;
        int bIdCardLength = tHallMakecard.getBIdcard().length();
        int aIdCardLength = 0;
        StringBuilder sb = new StringBuilder(tHallMakecard.getBIdcard());
        String bIdmsg = null;
        if(bIdCardLength >0 && bIdCardLength <10){
            bIdmsg = sb.replace(3,bIdCardLength-3,"*****").toString();
        }else if(bIdCardLength >9 && bIdCardLength <19) {
            bIdmsg = sb.replace(4,bIdCardLength-4,"*****").toString();
        }
        delmsg = tHallMakecard.getBName()+" "+bIdmsg;
        String aIdmsg = null;
        if(tHallMakecard.getAIdcard()!=null){
            aIdCardLength = tHallMakecard.getAIdcard().length();
            StringBuilder sb2 = new StringBuilder(tHallMakecard.getAIdcard());
            if(aIdCardLength >0 && aIdCardLength <10){
                aIdmsg = sb2.replace(3,aIdCardLength-3,"*****").toString();
            }else if(aIdCardLength >9 && aIdCardLength <19) {
                aIdmsg = sb2.replace(4,aIdCardLength-4,"*****").toString();
            }
            delmsg = tHallMakecard.getBName()+" "+bIdmsg+" "+aIdmsg;
        }
        //再读取文件
        FileInputStream in = null;
        try {
            in = new FileInputStream(CARD_FILE_PATH);
            //以指定的编码读取文件
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String s = null;
            int i = 0;
            while ((s = bufReader.readLine())!=null){
                if(s.equals(delmsg)){
                    i++;
                }else {
                    resultList.add(s+"\n");
                }
            }
            if(i>1){
                for(int j=1;j<i;j++){
                    resultList.add(0,delmsg+"\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        //开始写入文件，先清空
        try {
            FileWriter fw = new FileWriter(CARD_FILE_DIR + "\\" + CARD_FILE_NAME,false);
            fw.write("");
            // 关闭写文件,每次仅仅写一行数据，因为一个读文件中仅仅一个唯一的od
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
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
            throw new RuntimeException();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                out = null;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        return "修改证件成功！";
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
                bIdMsg = sb.replace(3,bIdCardLength-3,"*****").toString();
            }else if(bIdCardLength >9 && bIdCardLength <19) {
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
