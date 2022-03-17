package com.kuang.servlet.user;

import com.kuang.pojo.User;
import com.kuang.service.user.UserServiceImpl;
import com.kuang.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//业务层主要就是调用Dao层，并使用Dao层的方法，起一个中转站的作用。他自己本身并不做处理，只是去调用Dao层。
//servlet层：控制层，用来调用业务层代码
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //获取用户名和密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        //和数据库中的密码进行对比，调用业务层
        UserServiceImpl userService = new UserServiceImpl();
        //这一步很牛批，将前端取到的代码拿到数据库中去查一下，然后返回来一个值。
        User user = userService.login(userCode, userPassword);

        //如果user为空，说明没查到。要是不为空，说明查成功了！
        if(user!=null){
            //查成功之后将用户的信息放到Session中!
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }else{//查无此人
            //转发回登录页面，顺便提示它，用户名或者密码错误；
            req.setAttribute("error","用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
