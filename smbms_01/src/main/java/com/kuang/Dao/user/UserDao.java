package com.kuang.Dao.user;

import com.kuang.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserDao {
    //只要一个userCode就可以得到数据库的连接进行查找操作
    public User getLoginUser(Connection connection, String userCode,String userPassword) throws SQLException;

    //修改密码
    public int updatePwd(Connection connection, int id, String pwd) throws SQLException;

    //根据用户名或者角色查询用户总数
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException;

    //通过条件查询userList
    public List<User> getUserList(Connection connection,String userName,int userRole,int currentPageNo,int pageSize) throws SQLException;

    //增加用户
    public int add(Connection connection, User user);

    //判断用户编码与数据库中的编码是否一样
    public User userCodeExist(Connection connection, String userCode) throws SQLException;
}
