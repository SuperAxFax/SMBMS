package com.kuang.service.user;

import com.kuang.pojo.User;

public interface UserService {
    //得到用户登录的用户名和密码。
    public User login(String userCode, String password);

    //根据用户id修改密码
    public boolean updatePwd(int id,String pwd);
    
    //查询记录数
    public int getUserCount(String username, int userRole);

    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName,int queryUserRole,int currentPageNo,int pageSize);

    //增加用户
    public boolean add(User user);

    //判断用户编码是否与数据库中的重复
    public User userCodeExist(String userCode);

    //根据ID删除user
    public boolean deleteUserById(Integer delId);
    
    //修改用户信息
    public boolean modify(User user);

    //根据Id查找User
    public User getUserById(String id);
}

