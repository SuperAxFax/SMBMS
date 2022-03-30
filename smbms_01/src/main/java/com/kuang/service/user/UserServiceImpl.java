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

    //查询记录数
    public int getUserCount(String username, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection,username,userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return count;
    }

    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {

        List<User> userList = null;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection,null,null);
        }
        /*System.out.println("进入了service层");*/
        return userList;
    }

    //添加用户
    public boolean add(User user) {

        Boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.add(connection, user);
            connection.commit();

            if (updateRows>0){
                flag = true;
                System.out.println("修改成功！");
            }else {
                System.out.println("修改失败！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();//事务回滚
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }



    //判断输入编码与用户编码是否相同

    /*这个与ajax验证原密码不一样，那个由于密码已经放在了session中，直接从session中查找即可，而这个需要
    在数据库中进行查找，看是否有相同的userCode。所以需要从前往后一套流程下来！*/
    public User userCodeExist(String userCode) {
        User user = null;
        Connection connection = BaseDao.getConnection();
        try {
            user = userDao.userCodeExist(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
         return user;
    }


   /* @Test
   public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        User login = userService.login("test", "111");
        System.out.println(login.getUserPassword());
    }*/

}
