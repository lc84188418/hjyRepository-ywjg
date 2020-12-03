package com.hjy.system.service;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface WebSocketService {

    void IndexData(HttpServletRequest request) throws IOException;
}
