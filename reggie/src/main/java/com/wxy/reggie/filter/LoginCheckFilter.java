package com.wxy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wxy.reggie.common.R;
import com.wxy.reggie.config.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录过滤器
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();//进行路径匹配,支持通配符

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1获取url
        String requestURI = request.getRequestURI();
        log.info("Request URI: " + requestURI);
        //这这些请求放行
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2判断请求是否处理
        boolean check = check(urls, requestURI);
        //3如果不需要处理,放行
        if (check) {
            log.info("本次请求不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //4-1 判断后台登录状态， 如果已经登录直接放行，
        if ((request.getSession().getAttribute("employee") != null)) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("empId=" + empId);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //4-2 判断前台登录状态， 如果已经登录直接放行，
        if ((request.getSession().getAttribute("user") != null)) {
            Long userId = (Long) request.getSession().getAttribute("user");
            log.info("empId=" + userId);
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //5,没有登录，通过输出流的方式向客户端响应数据
        log.info("用户没有登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 检查当前本次请求是否需要放行
     *
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;

    }
}
