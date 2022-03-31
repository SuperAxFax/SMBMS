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
        }else if (method.equals("pwdmodify")&&method!=null){
            this.pwdModify(req,resp);
        }else if (method.equals("query")&&method!=null){
            this.query(req,resp);
        }else if (method.equals("add")&&method!=null){
            this.add(req,resp);
        }else if (method.equals("getrolelist") &&  method !=null){
            this.getRoleList(req,resp);
        }else if (method.equals("ucexist") && method != null){
            this.userCodeExist(req,resp);
        }else if (method.equals("view") && method != null){
            System.out.println("进入了view判断");
            this.getUserById(req,resp,"userview.jsp");
        }
        else if (method.equals("modifyexe") && method !=null){
            this.modify(req,resp);
        }else if (method.equals("modify") && method !=null){
            this.getUserById(req,resp,"usermodify.jsp");
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
    
    //验证旧密码,session中有用户的密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        //从Session里面拿ID
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);

        String oldpassword = req.getParameter("oldpassword");

        //万能的Map,结果集
        HashMap<String, String> resultMap = new HashMap<String, String>();

        if (o==null){//Session失效了，Session过期了！
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){//输入的旧密码为空
            resultMap.put("result","error");
        }else{
            String userPassword = ((User) o).getUserPassword();//得到Session中的密码
            //oldpassword是用户输入的旧密码，userPassword是Session中的登录密码。
            if (oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else{
                resultMap.put("result","false");
            }
        }
        //它的作用是让上面这个方法返回一个json值！
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            /*
            * resultMap = ["result","sessionerror","result","error"]
            * Json格式 = {key:value}
            * */
            //也就是把把resultMap转换成Json的格式！
            writer.write(JSONArray.toJSONString(resultMap));
            //刷新与关闭流！
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //重点，难点
    public void query(HttpServletRequest req, HttpServletResponse resp){

        //查询用户列表
        //从前端获取数据
        String queryname = req.getParameter("queryname");
        String queryUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int num = 0 ;//表示角色默认为0

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();

        //第一次走这个请求，一定是第一页，页面大小是固定的
        int pageSize = 5;  //可以把这个放到配置文件中，方便后期修改！
        int currentPageNo = 1;  //默认当前页面为1

        if (queryname == null){
            queryname = "";
        }if (queryUserRole !=null && !queryUserRole.equals("")){
             num = Integer.parseInt(queryUserRole);  //把角色解析为具体的数字
        }if (pageIndex!=null){
            currentPageNo= Integer.parseInt(pageIndex);//把页面解析为具体的数字
        }

        //获取用户总数（分页： 上一页，下一页的情况）
        int totalcount = userService.getUserCount(queryname, num);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalcount);

        int totalPageCount = ((int)(totalcount/pageSize))+1;//总共有几页

        //控制首页和尾页
        if (currentPageNo<1){
            currentPageNo = 1;
        }else if (currentPageNo>totalPageCount){
            currentPageNo = totalPageCount;
        }


        //获取用户列表展示
        List<User> userList = userService.getUserList(queryname, num, currentPageNo, pageSize);
        req.setAttribute("userList",userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalPageCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryname);
        req.setAttribute("queryUserRole",queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //增加用户
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        System.out.println("进入了servlet");
        User user = new User();
        user.setUserCode(req.getParameter("userCode"));
        user.setUserName(req.getParameter("userName"));
        user.setUserPassword(req.getParameter("userPassword"));
        user.setGender(Integer.valueOf(req.getParameter("gender")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("birthday")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(req.getParameter("phone"));
        user.setAddress(req.getParameter("address"));
        user.setUserRole(Integer.valueOf(req.getParameter("userRole")));
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setCreationDate(new Date());

        UserServiceImpl userService = new UserServiceImpl();
        if(userService.add(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }

    }

    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }


    //判断当前输入用户编码是否可用，即是否与已经存在的编码发生冲突
    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("进入了servlet方法");
        //先拿到用户的编码
        String userCode = req.getParameter("userCode");
        //用一个hashmap，暂存现在所有现存的用户编码
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)){//如果输入的编码为空或者不存在，说明可用
            resultMap.put("userCode","exist");
        }else{//如果输入的编码不为空，则需要去找一下是否存在这个用户
            UserServiceImpl userService = new UserServiceImpl();
            User user = userService.userCodeExist(userCode);
            if (user!=null){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode","notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter writer = resp.getWriter();
        //把resultMap转为json字符串并输出
        writer.write(JSONArray.toJSONString(resultMap));
        //刷新并关闭流
        writer.flush();
        writer.close();
    }
    
    //修改用户信息
    private void modify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //从前端获得用户信息并放到用户user中。
        System.out.println("进入了modify的servlet");
        User user = new User();
        user.setId(Integer.valueOf(req.getParameter("uid")));
        user.setUserName(req.getParameter("userName"));
        user.setGender(Integer.valueOf(req.getParameter("gender")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("birthday")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(req.getParameter("phone"));
        user.setAddress(req.getParameter("address"));
        user.setUserRole(Integer.valueOf(req.getParameter("userRole")));
        user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());
        //调用Service层中的modify方法对用户信息进行修改
        UserService userService = new UserServiceImpl();
        if (userService.modify(user)){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else {
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        }
        //用户信息包括userName,gender,birthday,phone,address,userRole
    }

    //根据id获得用户
    private void getUserById(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException {
        System.out.println("进入了getUserById的servlet");
        String id = req.getParameter("uid");
        User user = null;
        if (!StringUtils.isNullOrEmpty(id)){
            UserService userService = new UserServiceImpl();
            user = userService.getUserById(id);
            req.setAttribute("user",user);
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }

}

