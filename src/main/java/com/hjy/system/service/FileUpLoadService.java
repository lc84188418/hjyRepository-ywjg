package com.hjy.system.service;

import com.hjy.common.domin.CommonResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传服务接口
 *
 * @author wangdengjun
 * @since 2020-07-27 16:15:29
 */
public interface FileUpLoadService {


    CommonResult redListFileUploadSqs(MultipartFile file, HttpServletRequest httpRequest);
    CommonResult redListFileUploadZzjgdm(MultipartFile file, HttpServletRequest httpRequest);

    CommonResult beiancaijiZzjgdmz(MultipartFile file, HttpServletRequest httpRequest);
    CommonResult beiancaijiWts(MultipartFile file, HttpServletRequest httpRequest);

    CommonResult beiancaijiBy1(MultipartFile file, HttpServletRequest httpRequest);
    CommonResult beiancaijiBy2(MultipartFile file, HttpServletRequest httpRequest);


}