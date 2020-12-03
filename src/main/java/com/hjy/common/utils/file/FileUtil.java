package com.hjy.common.utils.file;

import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.PropertiesUtil;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传工具类
 */
public class FileUtil {

    /**
     * 判断是否为视频格式
     * @param file
     * @return
     */
    public static boolean isVideoFile(MultipartFile file){
        if(file !=null){
            //文件全名
            String fileName = file.getOriginalFilename();
            //文件名后缀，即文件类型
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String[] s ={".avi",".baimov",".rmvb",".rm",".flv",".mp4",".3gp",".mpeg",".asf",".wmv",".navi",".f4v",".mpg",".dat"};
            if(ArrayUtils.contains(s,suffixName.toLowerCase())){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断是否为文档格式
     * @param file
     * @return
     */
    public static boolean isDocFile(MultipartFile file){
        if(file !=null){
            //文件全名
            String fileName = file.getOriginalFilename();
            //文件名后缀，即文件类型
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String[] s ={".doc",".docx",".ppt",".pptx",".xls",".xlsx"};
            if(ArrayUtils.contains(s,suffixName.toLowerCase())){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }
    /**
     * 判断是否为图片格式
     * @param file
     * @return
     */
    public static boolean isPicFile(MultipartFile file){
        if(file !=null){
            //文件全名
            String fileName = file.getOriginalFilename();
            //文件名后缀，即文件类型
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String[] s ={".jpg",".png",".jpeg",".gif"};
            if(ArrayUtils.contains(s,suffixName.toLowerCase())){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    /**
     * 批量上传文件
     * @param files
     * @param username
     * @return
     */
    public static Map<String,Object> fileBatchUpload(MultipartFile[] files, String username,String url){
        Map<String,Object> pathMap = new HashMap<String,Object>();
        if(files!=null && files.length>0){
            for(int i = 0;i < files.length; i++){
                MultipartFile file = files[i];
                if(!file.isEmpty()){
                    String filePath = fileUpload(file,username);
                    pathMap.put("path"+i,url+filePath);
                }
            }
        }
        return pathMap;
    }

    /**
     * 单文件上传
     * @param file
     * @param username
     * @return
     */
    public static String fileUpload(MultipartFile file,String username){
        String upLoadPath = "";
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            upLoadPath = PropertiesUtil.getValue("file.upload.path.win");
        } else {
            upLoadPath = PropertiesUtil.getValue("file.upload.path.other");
        }
        String fileName = file.getOriginalFilename();
        //文件名后缀，即文件类型
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //获取时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String time = sdf.format(date.getTime());
        time = time +"/"+suffixName.replace(".","");
        //1.创建目录
        StringBuffer dirpath = new StringBuffer(time);
        dirpath.insert(0,upLoadPath);
        File targetFile = new File(dirpath.toString());
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File saveFile = new File(targetFile, username+IDUtils.getUUID()+suffixName.toLowerCase());
        try {
            file.transferTo(saveFile);
        } catch (Exception e) {
        }
        return saveFile.getPath().replace(upLoadPath,"/upload/");
    }

}
