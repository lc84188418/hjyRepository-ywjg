//package com.hjy.hall.webservice;
//
////import com.sun.xml.internal.txw2.Document;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.RequestEntity;
////import org.apache.poi.xwpf.usermodel.Document;
//import org.springframework.util.StringUtils;
////import org.w3c.dom.Element;
////import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
//import sun.plugin.dom.core.Document;
//
////import javax.lang.model.element.Element;
////import javax.swing.text.Document;
//import javax.swing.text.Element;
//import java.io.*;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author liuchun
// * @createDate 2020/11/13 16:59
// * @Classname WebServiceUtils
// * @Description TODO
// */
//public class WebServiceUtils {
//
//    public static void webServiceUtil(){
//        StringBuilder soap=new StringBuilder(); //构造请求报文
//        soap.append(" <soapenv:Envelope  xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
//        soap.append(" xmlns:wor=\"http://www.horizon.com/workflow/webservice/client/workflowCommon\">");
//        soap.append(" <soapenv:Header>");
//        soap.append(" <HZWFService  xmlns=\"http://www.huizhengtech.com/webservice/workflow\"");
//        soap.append(" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"");
//        soap.append(" SOAP-ENV:actor=\"http://www.w3.org/2003/05/soap-envelope/role/next\">admin&admin</HZWFService>");
//        soap.append(" </soapenv:Header>");
//        soap.append(" <soapenv:Body>");
//        soap.append(" <wor:sysLogin>");
//        soap.append(" <loginName>loginname</loginName >");
//        soap.append(" <password>password</password>");
//        soap.append(" <dbidentifier>system</dbidentifier>");
//        soap.append(" </wor:sysLogin>");
//        soap.append(" </soapenv:Body>");
//        soap.append(" </soapenv:Envelope>");
//        String requestSoap=soap.toString();
//
//        String serviceAddress="http://10.68.135.11:8082/WorkflowCommonService?wsdl";   //服务地址(将XXXX替换成自己项目的地址)
//        String charSet="utf-8";
//        String contentType="text/xml; charset=utf-8";
//        //第一步：调用方法getResponseSoap。返回响应报文和状态码</span>
////        <span style="font-size:24px;">
//
//        Map<String,Object> responseSoapMap = WebServiceUtils.responseSoap(requestSoap, serviceAddress, charSet, contentType);
//        Integer statusCode=(Integer)responseSoapMap.get("statusCode");
//        if(statusCode==200){
//            String responseSoap=(String)responseSoapMap.get("responseSoap");
//            String targetNodeName="isSuccess";
////                <span style="font-size:24px;">
////                    //第二步：调用strXmlToDocument方法。
////                    将字符串类型的XML的响应报文 转化成Docunent结构文档
////                    </span>
//            Document doc= WebServiceUtils.strXmlToDocument(responseSoap);
////                  <span style="font-size:24px;">
//                    //第三步：调用getValueByElementName方法。递归获得目标节点的值</span>
//            String result= WebServiceUtils.getValueByElementName(doc,targetNodeName);
//            if(!StringUtils.isEmpty(result)){
//                System.out.println(result);
//            }
//            else{
//                System.out.println("没有此节点或者没有值！");}
//        }
//        else{
//            System.out.println("请求失败！");
//        }
//    }
//
//    public static Map<String,Object> responseSoap(String requestSoap,String serviceAddress,String charSet, String contentType){
//        String responseSoap="";
//        Map<String,Object> resultmap = new HashMap<String,Object>();
//        PostMethod postMethod = new PostMethod(serviceAddress);
//        byte[] b = new byte[0];
//        try {
//            b = requestSoap.getBytes(charSet);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        InputStream is = new ByteArrayInputStream(b, 0, b.length);
//        RequestEntity re = new InputStreamRequestEntity(is, b.length, contentType);
//        postMethod.setRequestEntity(re);
//        HttpClient httpClient = new HttpClient();
//        int statusCode = 0;
//        try {
//            statusCode = httpClient.executeMethod(postMethod);
//            resultmap.put("statusCode", statusCode);
//        } catch (IOException e) {
//            throw new RuntimeException("执行http请求失败", e);
//        }
//        if (statusCode == 200) {
//            try {
//                responseSoap = postMethod.getResponseBodyAsString();
//                resultmap.put("responseSoap", responseSoap);
//            } catch (IOException e) {
//                throw new RuntimeException("获取请求返回报文失败", e);
//            }
//        } else {
//            throw new RuntimeException("请求失败：" + statusCode);
//        }
//        return resultmap;
//    }
//
//
//    public static Document strXmlToDocument(String parseStrXml){
//        StringReader read = new StringReader(parseStrXml);
//        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
//        InputSource source = new InputSource(read);
//        //创建一个新的SAXBuilder
//        SAXBuilder sb = new SAXBuilder();   // 新建立构造器
//        Document doc = null;
//        try {
//            doc = sb.build(source);
//        } catch (JDOMException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return doc;
//
//    }
//
//    public static String getValueByElementName(Document doc,String finalNodeName){
//        Element root = doc.getRootElement();
//        HashMap<String,Object> map=new HashMap<String,Object>();
////     <span style="font-size:24px;">   //调用getChildAllText方法。获取目标子节点的值 </span>
//        Map<String,Object> resultmap = WebServiceUtils.getChildAllText(doc, root,map);
//        String result=(String)resultmap.get(finalNodeName);
//        return result;
//    }
//
//    public static Map<String ,Object> getChildAllText(Document doc, Element e, HashMap<String,Object> resultmap) {
//        if (e != null)
//        {
//            if (e.getChildren() != null)   //如果存在子节点
//            {
//                List<Element> list = e.getChildren();
//                for (Element el : list)    //循环输出
//                {
//                    if(el.getChildren().size() > 0)   //如果子节点还存在子节点，则递归获取
//                    {
//                        getChildAllText(doc, el,resultmap);
//                    }
//                    else
//                    {
//                        resultmap.put(el.getName(), el.getTextTrim());  //将叶子节点值压入map
//                    }
//                }
//            }
//        }
//        return resultmap;
//    }
//}
