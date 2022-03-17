package com.kuang.Dao.user;

import com.kuang.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserDao {
    //只要一个userCode就可以得到数据库的连接进行查找操作
    public User getLoginUser(Connection connection, String userCode,String userPassword) throws SQLException;

    //修改密码
    public int updatePwd(Connection connection, int id, String pwd) throws SQLException;
}
