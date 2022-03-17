package com.kuang.servlet.user;

import com.kuang.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.getSession().removeAttribute(Constants.USER_SESSION);
            resp.sendRedirect(req.getContextPath()+"/login.jsp");
           /* req.getVContextPath()的作用是获得当前路径，不加的话点击退出的时候会跳到：http://localhost:8080/login.jsp
              加了之后会跳到http://localhost:8080/smbms/login.jsp*/
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
