package com.hjy.common.utils.file;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public  class MyFileUtil {


    //黑红名单添加时，文件单独上传使用
    public static String RedListFileUpload(MultipartFile file, String shijiancuo, String qianzhui) {
        StringBuffer dbfilePathsb = new StringBuffer();
        if (!file.isEmpty()) {
            //获取时间
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String time = sdf.format(date.getTime());
            String times[] = time.split("_");
            //拿到的该文件名
            String fileName = file.getOriginalFilename();
            //文件名后缀，即文件类型
            String suffixName = file.getOriginalFilename().substring(fileName.lastIndexOf("."));
            //重命名文件名
            dbfilePathsb.append(qianzhui + "_" + times[0] + "_" + times[1] + "_" + times[2] + "_" + shijiancuo + suffixName);
            //调用文件上传工具类
            String filePath = RedListFileUpload(dbfilePathsb.toString(), file);
        }
        //为文件1路径及其名称
        return dbfilePathsb.toString();
    }

    //红名单添加时，文件保存工具
    public static String RedListFileUpload(String fileName, MultipartFile file) {
        //获取时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String time = sdf.format(date.getTime());
        String times[] = time.split("_");
        //1.创建目录按时间分目录
//        StringBuffer dirpath = new StringBuffer(times[0] + "/" + times[1] + "/" + times[2]);
        //不分时间目录，全放在redList下
        StringBuffer dirpath = new StringBuffer();
        String filePath = null;
        dirpath.insert(0, "D:/hjy/ywjg/list/redList/");
        filePath = "D:/hjy/ywjg/list/redList/";
        File targetFile = new File(dirpath.toString());
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File saveFile = new File(targetFile, fileName);
        try {
            file.transferTo(saveFile);
        } catch (Exception e) {
        }
        return filePath;
    }

    //备案采集，文件单独上传使用
    public static String beiancaiji(MultipartFile file, String shijiancuo, String fenlei) {
        StringBuffer dbfilePathsb = new StringBuffer();
        if (!file.isEmpty()) {
            //获取时间
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            String time = sdf.format(date.getTime());
            String times[] = time.split("_");
            //拿到的该文件名
            String fileName = file.getOriginalFilename();
            //文件名后缀，即文件类型
            String suffixName = file.getOriginalFilename().substring(fileName.lastIndexOf("."));
            //重命名文件名
            dbfilePathsb.append(fenlei + "_" + times[0] + "_" + times[1] + "_" + times[2] + "_" + shijiancuo + suffixName);
            //调用文件上传工具类
            String filePath = BeiancaijiFileUpload(dbfilePathsb.toString(), file, fenlei);
        }
        //为文件1路径及其名称
        return dbfilePathsb.toString();
    }

    //备案添加时，文件保存工具
    public static String BeiancaijiFileUpload(String fileName, MultipartFile file, String fenlei) {
        //获取时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String time = sdf.format(date.getTime());
        String times[] = time.split("_");
        //不分时间目录，全放在一个目录下
        StringBuffer dirpath = new StringBuffer();
        String filePath = null;
        dirpath.insert(0, "D:/hjy/ywjg/ba/" + fenlei + "/");
        filePath = "D:/hjy/ywjg/ba/" + fenlei + "/";
        File targetFile = new File(dirpath.toString());
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File saveFile = new File(targetFile, fileName);
        try {
            file.transferTo(saveFile);
        } catch (Exception e) {
        }
        return filePath;
    }















    /**
     * 多文件和参数同时上传
     * @param files
     * @param zzjgdm
     * @return
     */
    //备案记录，文件保存逻辑处理
    public static String[] BAFileUtil(MultipartFile[] files, String zzjgdm) {
        StringBuffer dbfilePathsb = new StringBuffer();
        StringBuffer dbfilePathsb2 = new StringBuffer();
        String[] strings = new String[2];
        if (files != null && files.length > 0 && files.length <= 2) {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (!file.isEmpty()) {
                    //获取时间
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
                    String time = sdf.format(date.getTime());
                    String times[] = time.split("_");
                    //第一个文件为组织机构的 即i=0
                    //拿到的该文件名
                    String fileName = file.getOriginalFilename();
                    //判断是否为图片文件
                    if (PictureFileUtil(file)) {
                        //重命名文件名
                        fileName = times[0] + "_" + times[1] + "_" + times[2] + "_" + zzjgdm + ".jpg";
                        if (i == 0) {
                            dbfilePathsb.insert(0, "zzjgdm_");
                            dbfilePathsb.append(fileName);
                        } else {
                            dbfilePathsb2.insert(0, "wts_");
                            dbfilePathsb2.append(fileName);
                        }
                    } else {
                        //文件名后缀，即文件类型
                        String suffixName = file.getOriginalFilename().substring(fileName.lastIndexOf("."));
                        //重命名文件名
                        fileName = times[0] + "_" + times[1] + "_" + times[2] + "_" + zzjgdm + suffixName;
                        if (i == 0) {
                            dbfilePathsb.insert(0, "zzjgdm_");
                            dbfilePathsb.append(fileName);
                        } else {
                            dbfilePathsb2.insert(0, "wts_");
                            dbfilePathsb2.append(fileName);
                        }
                    }
                    //调用文件上传工具类
                    BAFileUpload(i, fileName, file);
                }
            }
        }
        //为文件1路径及其名称
        strings[0] = dbfilePathsb.toString();
        //为文件2路径及其名称
        strings[1] = dbfilePathsb2.toString();
        return strings;
    }

    //备案记录添加，文件保存工具
    public static void BAFileUpload(int i, String fileName, MultipartFile file) {
        //获取时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        String time = sdf.format(date.getTime());
        String times[] = time.split("_");
        //1.创建目录
        StringBuffer dirpath = new StringBuffer(times[0] + "/" + times[1] + "/" + times[2]);
        String filePath = null;
        if (i == 0) {
            dirpath.insert(0, "D:/hjy/ywjg/ba/zzjgdm/");
        } else {
            dirpath.insert(0, "D:/hjy/ywjg/ba/wts/");
        }
        File targetFile = new File(dirpath.toString());
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File saveFile = new File(targetFile, fileName);
        try {
            file.transferTo(saveFile);
        } catch (Exception e) {
        }
    }

    //红名单添加时，文件保存逻辑处理
    public static String[] ListFileUtil(MultipartFile[] files, String IDcard) {
        StringBuffer dbfilePathsb = new StringBuffer();
        StringBuffer dbfilePathsb2 = new StringBuffer();
        String[] strings = new String[2];
        if (files != null && files.length > 0 && files.length <= 2) {
            for (int i = 0, j = 0, k = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (!file.isEmpty()) {
                    //获取时间
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
                    String time = sdf.format(date.getTime());
                    String times[] = time.split("_");
                    //拿到的该文件名
                    String fileName = file.getOriginalFilename();

                    //文件名后缀，即文件类型
                    String suffixName = file.getOriginalFilename().substring(fileName.lastIndexOf("."));
                    k++;
                    //重命名文件名
                    if (k > 0) {
                        fileName = times[0] + "_" + times[1] + "_" + times[2] + "_" + IDcard + suffixName;
                        if (k == 1) {
                            dbfilePathsb.insert(0, "sqs_");
                            dbfilePathsb.append(fileName);
                        } else {
                            dbfilePathsb2.insert(0, "zzjgdm_");
                            dbfilePathsb2.append(fileName);
                        }
                    }
                    //调用文件上传工具类
                    String filePath = RedListFileUpload(fileName, file);
                }
            }
        }
        //为文件1路径及其名称
        strings[0] = dbfilePathsb.toString();
        //为文件2路径及其名称
        strings[1] = dbfilePathsb2.toString();
        return strings;
    }

    //判断是否为图片文件
    public static boolean PictureFileUtil(MultipartFile file) {
        if (file != null) {
            //文件全名
            String fileName = file.getOriginalFilename();
            //文件名后缀，即文件类型
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            if (suffixName.equals(".jpg")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    //删除文件

}
