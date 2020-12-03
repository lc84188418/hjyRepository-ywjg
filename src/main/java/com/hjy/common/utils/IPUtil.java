package com.hjy.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;

public class IPUtil {

    private static final String UNKNOWN = "unknown";

    protected IPUtil() {

    }
	private static final String[] HEADERS = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR",
			"X-Real-IP"
	};
	/**
	 * 判断ip是否为空，空返回true
	 * @param ip
	 * @return
	 */
	public static boolean isEmptyIp(final String ip){
		return (ip == null || ip.length() == 0 || ip.trim().equals("") || UNKNOWN.equalsIgnoreCase(ip));
	}
	/**
	 * 判断ip是否不为空，不为空返回true
	 * @param ip
	 * @return
	 */
	public static boolean isNotEmptyIp(final String ip){
		return !isEmptyIp(ip);
	}
	//获取客户端ip
	public static String getIpAddress(HttpServletRequest request) {
		String ip = null;
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//Proxy-Client-IP：apache 服务代理
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//WL-Proxy-Client-IP：weblogic 服务代理
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//HTTP_CLIENT_IP：有些代理服务器
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//X-Real-IP：nginx服务代理
			ipAddresses = request.getHeader("X-Real-IP");
		}
		//有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0];
		}
		//还是不能获取到，最后再通过request.getRemoteAddr();获取
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			ip = request.getRemoteAddr();
		}
//		IpCacheClass.getInstance();
//		String value = IpCacheClass.getCache(ip);
		return ip;
	}
	//获取服务端ip
	//此方法只能获取到该服务器所在机器的ip
	public static String getServiceIpAddress() {
		String ip = "";
		try {
			InetAddress ip4 = Inet4Address.getLocalHost();
			ip = ip4.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
