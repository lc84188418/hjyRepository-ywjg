package com.hjy.system.controller;

import com.hjy.common.domin.CommonResult;
import com.hjy.common.exception.FebsException;
import com.hjy.system.service.FileUpLoadService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传控制层
 *
 * @author wangdengjun
 * @since 2020-09-28 17:48:11
 */
@Slf4j
@RestController
public class FileUpLoadController {
    @Autowired
    private FileUpLoadService fileUpLoadService;


    /**
     *
     * 单个上传图片-申请书
     */
    @PostMapping("/file/upload/pic1")
    public CommonResult singlePicUpLoad1(MultipartFile file, HttpServletRequest httpRequest) throws FebsException {
        try {
            return fileUpLoadService.redListFileUploadSqs(file, httpRequest);
        } catch (Exception e) {
            String message = "上传失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 单个上传图片-组织机构代码证
     */
    @PostMapping("/file/upload/pic2")
    public CommonResult singlePicUpLoad2(MultipartFile file, HttpServletRequest httpRequest) throws FebsException {
        try {
            return fileUpLoadService.redListFileUploadZzjgdm(file, httpRequest);
        } catch (Exception e) {
            String message = "上传失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    /**
     *备案信息采集
     * 单个上传图片-组织机构代码证
     */
    @PostMapping("/file/upload/pic3")
    public CommonResult beiancaijiZzjgdmz(MultipartFile file, HttpServletRequest httpRequest) throws FebsException {
        try {
            return fileUpLoadService.beiancaijiZzjgdmz(file, httpRequest);
        } catch (Exception e) {
            String message = "上传失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 备案信息采集
     * 单个上传图片-委托书
     */
    @PostMapping("/file/upload/pic4")
    public CommonResult beiancaijiWts(MultipartFile file, HttpServletRequest httpRequest) throws FebsException {
        try {
            return fileUpLoadService.beiancaijiWts(file, httpRequest);
        } catch (Exception e) {
            String message = "上传失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     *备案信息采集
     * 单个上传图片-备用1
     */
    @PostMapping("/file/upload/beiancaijiBy1")
    public CommonResult beiancaijiBy1(MultipartFile file, HttpServletRequest httpRequest) throws FebsException {
        try {
            return fileUpLoadService.beiancaijiBy1(file, httpRequest);
        } catch (Exception e) {
            String message = "上传失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }
    /**
     * 备案信息采集
     * 单个上传图片-备用2
     */
    @PostMapping("/file/upload/beiancaijiBy2")
    public CommonResult beiancaijiBy2(MultipartFile file, HttpServletRequest httpRequest) throws FebsException {
        try {
            return fileUpLoadService.beiancaijiBy2(file, httpRequest);
        } catch (Exception e) {
            String message = "上传失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

}