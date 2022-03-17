package com.kuang.filter;

import com.kuang.pojo.User;
import com.kuang.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        //由于req不能直接实现得到Session，resp不能直接实现sendRedirect转发。所以我们要把他们转成HttpServletRequest,HttpServletResponse的形式
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //过滤器，从Session中获取用户，一旦发现Session被移出为空了，也就是用户点击了退出的操作。那就返回错误页面。

        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        if (user==null){//session已经退出后被清除了，或者还未登录
            response.sendRedirect("/smbms/error.jsp");
        }else{//没被清除并且已经登录了。就接着继续往下执行
            chain.doFilter(req,resp);
        }
    }

    public void destroy() {

    }
}
