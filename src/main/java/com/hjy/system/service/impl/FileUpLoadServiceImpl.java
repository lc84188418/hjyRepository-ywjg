package com.hjy.system.service.impl;

import com.hjy.common.domin.CommonResult;
import com.hjy.common.utils.IDUtils;
import com.hjy.common.utils.TokenUtil;
import com.hjy.common.utils.file.FileUtil;
import com.hjy.common.utils.file.MyFileUtil;
import com.hjy.system.entity.SysToken;
import com.hjy.system.service.FileUpLoadService;
import com.hjy.system.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传服务实现类
 *
 * @author wangdengjun
 * @since 2020-07-27 16:15:29
 */
@Service
public class FileUpLoadServiceImpl implements FileUpLoadService {

    @Autowired
    private ShiroService shiroService;
    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.boot.application.ip}")
    private String webIp;
    @Override
    public CommonResult redListFileUploadSqs(MultipartFile file, HttpServletRequest httpRequest) {
        if (!FileUtil.isPicFile(file)) {
            return new CommonResult(201, "success", "非图片格式文件，不能上传", null);
        }
        Map<String, Object> pathMap = new HashMap<String, Object>();
        //时间戳
        String shijiancuo = String.valueOf(System.currentTimeMillis());
        String fenlei = "sqs";
        String filePath = MyFileUtil.RedListFileUpload(file,shijiancuo,fenlei);
        //文件显示路径src
        StringBuffer zzjgdmzPath = new StringBuffer();
        zzjgdmzPath.append("http://"+webIp+":"+serverPort+"/img/"+filePath);
        pathMap.put("path", zzjgdmzPath.toString());
        return new CommonResult(200, "success", "上传成功!", pathMap);
    }
    @Override
    public CommonResult redListFileUploadZzjgdm(MultipartFile file, HttpServletRequest httpRequest) {
        if (!FileUtil.isPicFile(file)) {
            return new CommonResult(201, "success", "非图片格式文件，不能上传", null);
        }
        Map<String, Object> pathMap = new HashMap<String, Object>();
        //时间戳
        String shijiancuo = String.valueOf(System.currentTimeMillis());
        String fenlei = "zzjgdm";
        String filePath = MyFileUtil.RedListFileUpload(file,shijiancuo,fenlei);
        //文件显示路径src
        StringBuffer zzjgdmzPath = new StringBuffer();
        zzjgdmzPath.append("http://"+webIp+":"+serverPort+"/img/"+filePath);
        pathMap.put("path", zzjgdmzPath.toString());
        return new CommonResult(200, "success", "上传成功!", pathMap);
    }
    @Override
    public CommonResult beiancaijiZzjgdmz(MultipartFile file, HttpServletRequest httpRequest) {
        if (!FileUtil.isPicFile(file)) {
            return new CommonResult(201, "success", "非图片格式文件，不能上传", null);
        }
        Map<String, Object> pathMap = new HashMap<String, Object>();
        //时间戳
        String shijiancuo = String.valueOf(System.currentTimeMillis());
        String fenlei = "zzjgdm";
        String filePath = MyFileUtil.beiancaiji(file,shijiancuo,fenlei);
        //文件显示路径src
        StringBuffer zzjgdmzPath = new StringBuffer();
        zzjgdmzPath.append("http://"+webIp+":"+serverPort+"/img/"+filePath);
        pathMap.put("path", zzjgdmzPath.toString());
        return new CommonResult(200, "success", "上传成功!", pathMap);
    }
    @Override
    public CommonResult beiancaijiWts(MultipartFile file, HttpServletRequest httpRequest) {
        if (!FileUtil.isPicFile(file)) {
            return new CommonResult(201, "success", "非图片格式文件，不能上传", null);
        }
        Map<String, Object> pathMap = new HashMap<String, Object>();
        //时间戳
        String shijiancuo = String.valueOf(System.currentTimeMillis());
        String fenlei = "wts";
        String filePath = MyFileUtil.beiancaiji(file,shijiancuo,fenlei);
        //文件显示路径src
        StringBuffer wtsPath = new StringBuffer();
        wtsPath.append("http://"+webIp+":"+serverPort+"/img/"+filePath);
        pathMap.put("path", wtsPath.toString());
        return new CommonResult(200, "success", "上传成功!", pathMap);
    }
    @Override
    public CommonResult beiancaijiBy1(MultipartFile file, HttpServletRequest httpRequest) {
        if (!FileUtil.isPicFile(file)) {
            return new CommonResult(201, "success", "非图片格式文件，不能上传", null);
        }
        Map<String, Object> pathMap = new HashMap<String, Object>();
        //时间戳
        String shijiancuo = String.valueOf(System.currentTimeMillis());
        String fenlei = "by1";
        String filePath = MyFileUtil.beiancaiji(file,shijiancuo,fenlei);
        //文件显示路径src
        StringBuffer by1Path = new StringBuffer();
        by1Path.append("http://"+webIp+":"+serverPort+"/img/"+filePath);
        pathMap.put("path", by1Path.toString());
        return new CommonResult(200, "success", "上传成功!", pathMap);
    }
    @Override
    public CommonResult beiancaijiBy2(MultipartFile file, HttpServletRequest httpRequest) {
        if (!FileUtil.isPicFile(file)) {
            return new CommonResult(201, "success", "非图片格式文件，不能上传", null);
        }
        Map<String, Object> pathMap = new HashMap<String, Object>();
        //时间戳
        String shijiancuo = String.valueOf(System.currentTimeMillis());
        String fenlei = "by2";
        String filePath = MyFileUtil.beiancaiji(file,shijiancuo,fenlei);
        //文件显示路径src
        StringBuffer by2Path = new StringBuffer();
        by2Path.append("http://"+webIp+":"+serverPort+"/img/"+filePath);
        pathMap.put("path", by2Path.toString());
        return new CommonResult(200, "success", "上传成功!", pathMap);
    }

}