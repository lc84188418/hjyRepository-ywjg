package com.hjy.common.config.websocket;

import com.alibaba.fastjson.JSONObject;
import com.hjy.common.utils.PropertiesUtil;
import com.hjy.system.entity.SysToken;
import com.hjy.system.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/webSocket/{token}")
//此注解相当于设置访问URL
public class WebSocket {

    @Autowired
    private ShiroService shiroService;
    private static WebSocket ntClient;
    private Session session;
    private String ip;
    private String username;

    private static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<>();
    //预警时特定发送对象集合
    private static String [] ips = PropertiesUtil.getValue("webSocket.warning.ip").split("/");

    @PostConstruct
    public void init() {
        ntClient = this;
        ntClient.shiroService = this.shiroService;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam(value="token")String token) {
        this.session = session;
        webSockets.add(this);
        //通过token查询ip地址
        SysToken token1 = ntClient.shiroService.findByToken(token);
        if (token1!=null){
            String ip = token1.getIp();
            this.ip = ip;
            this.username = token1.getUsername();
        }
        sessionPool.put(ip, session);
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
    }

    @OnMessage
    public void onMessage(String message) {
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for(WebSocket webSocket : webSockets) {
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //预警时，发送特定对象
    public void sendWarningMessage(String message) {
        for(String ip : ips) {
            Session session = sessionPool.get(ip);
            if (session != null) {
                try {
                    //
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    // 排队叫号时固定发送对象 synchronized
    public synchronized void sendTextMessageTo(String message) {
        JSONObject jsonObject = new JSONObject();
        String ip = PropertiesUtil.getValue("webSocket.callNum.ip");
        Session session = sessionPool.get(ip);
        jsonObject.put("call",message);
        if (session != null) {
            try {
                //
                session.getAsyncRemote().sendText(jsonObject.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 获取所有连接对象的ip
    public List<String> getAllIp() {
        List<String> ips = new ArrayList<>();
        for(WebSocket webSocket : webSockets) {
            ips.add(webSocket.ip);
        }
        return ips;
    }

}

