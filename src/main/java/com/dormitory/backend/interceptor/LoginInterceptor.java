package com.dormitory.backend.interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
//
//        if (session.getAttribute("username") == null) {
//            httpServletResponse.sendRedirect("/api/login");
//            return false; // 阻止请求继续执行
//        }
//
//        // 用户已登录，允许请求继续执行
//        return true;

        // 检查用户是否已登录
//        if (session.getAttribute("username") != null) {
//            return true;
//        } else {
//            // 用户未登录，返回未授权的响应
////            System.out.println("UNAUTH");
//            return false;
//        }
        return true;
    }
}
