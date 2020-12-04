package com.hjy.common.config.aspect;

import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjy.common.annotation.OperLog;
import com.hjy.common.utils.*;
import com.hjy.log.entity.TLogException;
import com.hjy.log.entity.TLogRecord;
import com.hjy.log.service.TLogExceptionService;
import com.hjy.log.service.TLogRecordService;
import com.hjy.system.dao.TSysTokenMapper;
import com.hjy.system.entity.ActiveUser;
import com.hjy.system.entity.SysToken;
import com.hjy.system.entity.TSysUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author liuchun
 * @date 2019/03/21
 */
@Aspect
@Component
public class OperLogAspect {

    /**
     * 操作版本号
     */
    @Value("${spring.boot.application}")
    private String operVer;

    @Autowired
    private TLogRecordService operationLogService;
    @Autowired
    private TLogExceptionService exceptionLogService;
    @Autowired
    private TSysTokenMapper tSysTokenMapper;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.hjy.common.annotation.OperLog)")
    public void operLogPoinCut() {
    }

    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("execution(* com.hjy.*.controller..*.*(..))")
    public void operExceptionLogPoinCut() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param keys      返回结果
     */
    @AfterReturning(value = "operLogPoinCut()", returning = "keys")
    public void saveOperLog(JoinPoint joinPoint, Object keys) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        TLogRecord logRecord = new TLogRecord();
        try {
            logRecord.setPkRecordId(IDUtils.getUUID()); // 主键ID
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            OperLog opLog = method.getAnnotation(OperLog.class);
            if (opLog != null) {
                String operModul = opLog.operModul();
                String operType = opLog.operType();
                String operDesc = opLog.operDesc();
                // 操作模块
                logRecord.setRecordModule(operModul);
                // 操作类型
                logRecord.setRecordType(operType);
                // 操作描述
                logRecord.setRecordDesc(operDesc);
            }
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            logRecord.setRecordMethod(methodName); // 请求方法
            // 请求的参数
//            Map<String, String> rtnMap = converMap(request.getParameterMap());
//            // 将参数所在的数组转换成json
//            String params = JSON.toJSONString(rtnMap);
//            logRecord.setRecordRequParam(StrBufferUtil.handTooLengthStrBuffer(params));
            // 返回结果
//            logRecord.setRecordRespParam(JSON.toJSONString(keys)); // 返回结果
            ActiveUser activeUser = UserShiroUtil.getActiveUser(request);
            if(activeUser != null){
                // 请求用户ID
                logRecord.setRecordUserId(activeUser.getUserId());
                // 请求用户账户名称
                logRecord.setRecordUserName(activeUser.getUsername());
                // 请求用户姓名
                logRecord.setRecordFullName(activeUser.getFullName());
            }else {
                String tokenStr = TokenUtil.getRequestToken(request);
                SysToken token = tSysTokenMapper.findByToken(tokenStr);
                // 请求用户ID
                logRecord.setRecordUserId(token.getFkUserId());
                // 请求用户账户名称
                logRecord.setRecordUserName(token.getUsername());
                // 请求用户姓名
                logRecord.setRecordFullName(token.getFullName());
            }
            // 请求IP
            logRecord.setRecordIp(IPUtil.getIpAddress(request));
            // 请求URI
            logRecord.setRecordUrl(request.getRequestURI());
            // 创建时间
            logRecord.setRecordTime(new Date());
            // 操作版本
            logRecord.setRecordVersion(operVer);
            operationLogService.insert(logRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     *
     * @param joinPoint 切入点
     * @param e         异常信息
     */
    @AfterThrowing(pointcut = "operExceptionLogPoinCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        TLogException excepLog = new TLogException();
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            excepLog.setPkExcId(IDUtils.getUUID());
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            // 请求的参数
//            Map<String, String> rtnMap = converMap(request.getParameterMap());
//            // 将参数所在的数组转换成json
//            String params = JSON.toJSONString(rtnMap);
//            excepLog.setExcRequParam(StrBufferUtil.handTooLengthStrBuffer(params));
            // 请求方法名
            excepLog.setOperMethod(methodName);
            // 异常名称
            excepLog.setExcName(e.getClass().getName());
            // 异常信息
            excepLog.setExcMsg(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace()));
            ActiveUser activeUser = UserShiroUtil.getActiveUser(request);
            if(activeUser != null){
                // 操作员ID
                excepLog.setOperUserId(activeUser.getUserId());
                // 操作员账户名称
                excepLog.setOperUserName(activeUser.getUsername());
                // 请求用户姓名
                excepLog.setOperFullName(activeUser.getFullName());
            }else {
                String tokenStr = TokenUtil.getRequestToken(request);
                SysToken token = tSysTokenMapper.findByToken(tokenStr);
                // 操作员ID
                excepLog.setOperUserId(token.getFkUserId());
                // 操作员账户名称
                excepLog.setOperUserName(activeUser.getUsername());
                // 请求用户姓名
                excepLog.setOperFullName(activeUser.getFullName());
            }
            // 操作URl
            excepLog.setOperUrl(request.getRequestURI());
            // 操作员IP
            excepLog.setOperIp(IPUtil.getIpAddress(request));
            // 操作版本号
            excepLog.setOperVersion(operVer);
            // 发生异常时间
            excepLog.setExcTime(new Date());
            exceptionLogService.insert(excepLog);

        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */
    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
//        for (String key : paramMap.keySet()) {
//            rtnMap.put(key, paramMap.get(key)[0]);
//        }
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
//            String cs = entry.getKey() + ":"+Arrays.toString(entry.getValue())+";";
            rtnMap.put(entry.getKey(),Arrays.toString(entry.getValue())+";");
        }
        return rtnMap;
    }

    /**
     * 转换异常信息为字符串
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
//        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        String message = exceptionMessage;
        return message;
    }
}
