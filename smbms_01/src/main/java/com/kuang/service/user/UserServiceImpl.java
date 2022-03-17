package com.kuang.service.user;

import com.kuang.Dao.BaseDao;
import com.kuang.Dao.user.UserDao;
import com.kuang.Dao.user.UserDaoImpl;
import com.kuang.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;


/*
* 这个类的主要思想就是通过业务层调用Dao层的getLoginUser方法进而得到user的信息。像调用Dao层以及调用公共类中的
* getConnection方法都是为了获得getLoginUser的参数。
* */
public class UserServiceImpl implements UserService {

    //业务层都会调用dao层，所以我们要引入Dao层；
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体的数据库操作
            user = userDao.getLoginUser(connection, userCode,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }


    public boolean updatePwd(int id, String pwd) {
        boolean flag = false;
        Connection connection = null;
        //如果调用Dao层以后得到的execute为1，也就是大于0.就说明修改成功。否则就修改失败！
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection,id,pwd)>0){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }


   /* @Test
   public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        User login = userService.login("test", "111");
        System.out.println(login.getUserPassword());
    }*/

}