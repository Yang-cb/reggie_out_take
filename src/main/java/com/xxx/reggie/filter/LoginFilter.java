package com.xxx.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.xxx.reggie.common.R;
import com.xxx.reggie.common.ThreadContextId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * reggie_take_out.com.xxx.reggie.filter
 *
 * @author : yang_
 * @description : 检查用户是否已经登录
 * @date : 2023/4/17 13:01
 */
@Slf4j
@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    //路径匹配器，用于匹配两个路径是否一致。支持通配符。
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 不需要拦截的请求：
        String[] notFilterUrls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };

        //1,获取本次请求的url
        String requestUri = request.getRequestURI();

        //2,判断本次请求是否需要拦截
        if (notFilter(requestUri, notFilterUrls)) {
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        //3.1,判断 管理端 用户是否已经登录
        Long empId = (Long)request.getSession().getAttribute("employee");
        if (empId != null) {
            // 将id添加到处理该业务的线程的变量中
            ThreadContextId.setContextId(empId);
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        //3.2,判断 移动端 用户是否已经登录
        Long userId = (Long)request.getSession().getAttribute("user");
        if (userId != null) {
            // 将id添加到处理该业务的线程的变量中
            ThreadContextId.setContextId(userId);
            // 放行
            filterChain.doFilter(request, response);
            return;
        }

        //4,拦截请求，并响应前端
        log.info("拦截到请求：{}", requestUri);
        // NOTLOGIN 是前端需要的格式
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 判断本次请求是否需要拦截
     *
     * @param requestUri 本次请求的uri
     * @param noFilterUrls 不需要拦截的请求
     * @return true ：不拦截
     */
    private boolean notFilter(String requestUri, String[] noFilterUrls) {
        for (String url : noFilterUrls) {
            // 不需要拦截
            if (ANT_PATH_MATCHER.match(url, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
