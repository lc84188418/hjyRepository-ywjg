package com.hjy.common.utils.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hjy.common.utils.PropertiesUtil;
import com.hjy.synthetical.entity.TSyntheticalMakecard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public class SmbFileUtil {

    private static Logger log = LoggerFactory.getLogger(SmbFileUtil.class);
    //字节长度
    private static final int byteLen = 1024;

    private static String USER_DOMAIN = null;
    private static String USER_ACCOUNT = "hjyshare";
    private static String USER_PWS = "hjyshare";

    /**
     * @Title listFiles
     * @Description 遍历指定目录下的文件
     * @date 2019-01-11 09:56
     */
    public static void smbFile(String shareDirectory) throws Exception {
        // 域服务器验证
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(USER_DOMAIN, USER_ACCOUNT,
                USER_PWS);
        SmbFile remoteFile = new SmbFile(shareDirectory, auth);
        if (remoteFile.exists()) {
//            SmbFile[] files = remoteFile.listFiles();
//            remoteFile.listFiles(shareDirectory);
//            for (SmbFile f : files) {
//                log.info(f.getName());
//            }
            //共享目录中的文件路径，如smb://132.20.2.33/CIMPublicTest/eg.txt
            String shareUrl = PropertiesUtil.getValue("share.file.path");
            //本地目录，如tempStore/smb
            String localDirectory = "d://hjy//ywjg//makeCard";
            //将共享文件下载到本地
            SmbFileUtil.smbGet(shareUrl,localDirectory);
            //将本地文件上传到共享文件
            SmbFileUtil.smbPut(PropertiesUtil.getValue("share.file.directory"),PropertiesUtil.getValue("makeCard.file.local.path"));
        } else {
            log.info("文件不存在");
        }
    }
    /**
     * @Title smbGet
     * @Param shareUrl 共享目录中的文件路径，如smb://132.20.2.33/CIMPublicTest/eg.txt
     * @Param localDirectory 本地目录，如tempStore/smb
     */
    public static void smbGet(String shareUrl, String localDirectory) throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(USER_DOMAIN, USER_ACCOUNT,
                USER_PWS);
        SmbFile remoteFile = new SmbFile(shareUrl, auth);
        if (!remoteFile.exists()) {
            log.info("共享文件不存在");
            return;
        }
        // 有文件的时候再初始化输入输出流
        InputStream in = null;
        OutputStream out = null;
        log.info("下载共享目录的文件 shareUrl 到 localDirectory");
        try {
            String fileName = remoteFile.getName();
            File localFile = new File(localDirectory + File.separator + fileName);
            File fileParent = localFile.getParentFile();
            if (null != fileParent && !fileParent.exists()) {
                fileParent.mkdirs();
            }
            in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[1024];
            }
            out.flush(); //刷新缓冲区输出流
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            in.close();
        }
    }


    /**
     *
     * @Title smbPut
     * @Description 向共享目录上传文件
     * @Param shareDirectory 共享目录
     * @Param localFilePath 本地目录中的文件路径
     * @date 2019-01-10 20:16
     */
    public static void smbPut(String shareDirectory, String localFilePath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File localFile = new File(localFilePath);
            String fileName = localFile.getName();
            // 域服务器验证
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(USER_DOMAIN, USER_ACCOUNT,
                    USER_PWS);
            SmbFile remoteFile = new SmbFile(shareDirectory + "/" + fileName, auth);
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[byteLen];
            while (in.read(buffer) != -1) {
                out.write(buffer);
                buffer = new byte[byteLen];
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}