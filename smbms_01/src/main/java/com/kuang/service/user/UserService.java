package com.kuang.service.user;

import com.kuang.pojo.User;

public interface UserService {
    //得到用户登录的用户名和密码。
    public User login(String userCode, String password);

    //根据用户id修改密码
    public boolean updatePwd(int id,String pwd);
}

