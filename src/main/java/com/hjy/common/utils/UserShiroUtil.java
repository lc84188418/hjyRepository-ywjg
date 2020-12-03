package com.hjy.common.utils;

import com.hjy.system.entity.ActiveUser;
import com.hjy.system.entity.TSysUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserShiroUtil {

    public static String getCurrentUserName(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        return activeUser.getFullName();
    }
    public static String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        return activeUser.getUserId();
    }
    public static ActiveUser getActiveUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
        return activeUser;
    }
}
