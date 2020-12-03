package com.hjy.common.utils.Http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import com.alibaba.fastjson.JSON;

public class HttpClient4 {

    // 向第三方接口发送一个post 请求的参数的看具体的要求，该接口想要的数据是什么类型，如果是json，那就把参数转换为json类型，其他的转换为其它类型，如阿里的接口参数就有的不是json类型
    public static String sendPost(String url,Map<String ,Object> paramMap) throws Exception {
        // json格式的参数，我们可以用map来封装参数，然后将参数转换为json格式
        String paramsJson = JSON.toJSONString(paramMap); // 将参数转换为json字符串
        //String params="{\"articleID\":44,\"columnID\":44,\"desColumnID\":44,\"title\":\"44\",\"subTitle\":\"44\",\"leadTitle\":\"44\",\"abstract\":\"44\",\"articleType\":0,\"source\":44\",\"tag\":\"44\",\"optUser\":\"44\",\"author\":\"44\",\"editor\":\"44\",\"liability\":\"44\",\"status\":1,\"content\":\"44\",\"imgUrl\":\"http://dev-file.aimingtai.com/webdata/45/images/2018/0829/15355154473121002.jpg\"}";
        // 服务端通常是根据请求头（headers）中的 Content-Type 字段来获知请求中的消息主体是用何种方式编码，再对主体进行解析。所以说到 POST 提交数据方案，包含了 Content-Type 和消息主体编码方式两部分
        HttpClient client = new HttpClient(); // 客户端实例化
        PostMethod postMethod = new PostMethod(url); // 请求方法post，可以将请求路径传入构造参数中
        postMethod.addRequestHeader("Content-type", "application/json; charset=utf-8");
        byte[] requestBytes = paramsJson.getBytes("utf-8"); // 将参数转为二进制流
        InputStream inputStream = new ByteArrayInputStream(requestBytes, 0,
                requestBytes.length);
        RequestEntity requestEntity = new InputStreamRequestEntity(inputStream,
                requestBytes.length, "application/json; charset=utf-8"); // 请求体
        postMethod.setRequestEntity(requestEntity);   // 将参数放入请求体
        int i = client.executeMethod(postMethod);  // 执行方法
        if(i !=200){
            return "请求文件服务器失败";
        }
        return "请求文件服务器成功";
        // 这里因该有判断的，根据请求状态判断请求是否成功，然后根据第三方接口返回的数据格式，解析出我们需要的数据
        // 得到响应数据
//        byte[] responseBody = postMethod.getResponseBody();
////        String s = new String(responseBody);
    }
}
