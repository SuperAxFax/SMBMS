package com.kuang.Dao.user;

import com.kuang.Dao.BaseDao;
import com.kuang.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
* 这个类的主要作用就是：
* 1：在重写的得到用户的方法中调用公共类中的execute查询方法进行查询。
* 2：然后将查询到的数据放到new出来的user用户中。
* 3：最后将user返回给调用者。。这样调用者就得到了userCode这个用户的所有信息。
* */
public class UserDaoImpl implements UserDao {
    //得到要登录的用户,
    public User getLoginUser(Connection connection, String userCode,String userPassword) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if (connection!=null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            //System.out.println(userPassword);
            rs = BaseDao.execute(connection,sql,params,rs,pstm);
            if (rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
           BaseDao.closeResource(null,pstm,rs);
            //这一句所解决的问题就是如果输入的密码与数据库中的密码不一样，，那么就不会登录进去
            //如果没有这句话，只要用户输入的用户名是对的，那么用户无论输入什么密码都可以登录进去！
             if (!user.getUserPassword().equals(userPassword))
                user=null;
        }
        return user;
    }

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String pwd) throws SQLException {

        PreparedStatement preparedStatement = null;
        int execute = 0;
        if (connection!=null){
//public static int execute(Connection connection,String sql,Object[] params,PreparedStatement preparedStatement)
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object params[] = {pwd,id};
            execute = BaseDao.execute(connection, sql, params, preparedStatement);
            BaseDao.closeResource(null,preparedStatement,null);
        }
        return execute;
    }
}