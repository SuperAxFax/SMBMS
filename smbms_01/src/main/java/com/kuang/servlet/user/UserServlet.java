package com.kuang.servlet.user;

import com.kuang.pojo.User;
import com.kuang.service.user.UserService;
import com.kuang.service.user.UserServiceImpl;
import com.kuang.util.Constants;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //先从Session中拿到ID,并从前端拿到用户输入的新密码
        System.out.println("进入到了方法！");
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");

        boolean flag = false;
        //进行判断sesssion与新密码是否为空。并调用service层
        if (o!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), newpassword);

            //如果修改成功给出提示并移除session
            if (flag){
                req.setAttribute("message","密码修改成功！");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else{//如果修改失败给出提示
                req.setAttribute("message","密码修改失败");
            }

        }else{
            req.setAttribute("message","数据有错误！");
        }
        resp.sendRedirect(req.getContextPath()+"/jsp/pwdmodify.jsp");
        /*
        气死劳资了，你这个狗请求转发，吊用没有！
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);*/
        //如果为空也给出提示
        //最后转发到当前页面
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd")){
            this.updatePwd(req,resp);
        }
    }


    public void updatePwd(HttpServletRequest req, HttpServletResponse resp){
        System.out.println("进入到了方法！");
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");

        boolean flag = false;
        //进行判断sesssion与新密码是否为空。并调用service层
        if (o!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), newpassword);
            if (flag){
                req.setAttribute("message","密码修改成功！");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else{//如果修改失败给出提示
                req.setAttribute("message","密码修改失败");
            }
        }else{
            req.setAttribute("message","数据有错误！");
        }
        try {
            resp.sendRedirect(req.getContextPath()+"/jsp/pwdmodify.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

