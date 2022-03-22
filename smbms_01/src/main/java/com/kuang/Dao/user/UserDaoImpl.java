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
    
    //根据用户名或者角色查询用户总数【最难理解的SQL】
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {

        int count = 0;
        ResultSet rs = null;
        if (connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u, smbms_role r where u.userRole = r.id");
            ArrayList<Object> list = new ArrayList<Object>();//存放我们的参数

            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like  ?");
                list.add("%"+username+"%");//index:0
            }
            if (userRole>0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);//index:1
            }
            //把list转换为数组，然后作为参数传入！
            Object[] params = list.toArray();
            /*System.out.println("UserDaoImpl->getUserCount"+sql.toString());*///输出最后完整的SQL语句

            rs = BaseDao.execute(connection, sql.toString(), params, null, null);

            if (rs.next()){
                count = rs.getInt("count");//从结果集中获取最终的数量
            }
            BaseDao.closeResource(connection,null,rs);
        }
        return count;
    }

    //通过条件查询userList
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {
        /*System.out.println("Dao方法首部");*/
        ResultSet resultSet = null;
        PreparedStatement pstm = null;
        ArrayList<User> userList = new ArrayList<User>();
        if (connection!=null){
            /*
             StringBuffer，是可以存储和操作字符串，即包含多个字符的字符串数据。
             String类是字符串常量，是不可更改的常量。而StringBuffer是字符串变量，它的对象是可以扩充和修改的。
            */
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u, smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if (userRole>0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            /*
            * 在数据库中，分页使用 limit startIndex,pageSize；
            * 当前页：就为（当前页编号-1）*pageSize
            * 0,5
            * 5,5
            * 10,5
            * */
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();

            /*System.out.println("sql-->"+ sql.toString());*/
            resultSet = BaseDao.execute(connection, sql.toString(), params, resultSet, pstm);
            while(resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setUserRole(resultSet.getInt("userRole"));
                //用户角色名称
                user.setUserRoleName(resultSet.getString("userRoleName"));
                userList.add(user);
            }
            /*System.out.println("到达了Dao层");*/
            BaseDao.closeResource(connection,pstm,resultSet);
        }
        return userList;
    }


    //增加用户
    public int add(Connection connection, User user) {
        System.out.println("进入add的dao");
        int updateRows = 0;
        PreparedStatement preparedStatement = null;
        if (connection!=null){
            String sql = "insert into smbms_user (userCode,userName,userPassword," +
                    "gender,birthday,phone,address,userRole,createBy,creationDate)" +
                    "values(?,?,?,?,?,?,?,?,?,?)" ;

            Object[] params = {user.getUserCode(),user.getUserName(),user.getUserPassword(),
                    user.getGender(),user.getBirthday(),user.getPhone(),user.getAddress(),user.getUserRole(),
                    user.getCreatedBy(),user.getCreationDate()};
            try {
                updateRows = BaseDao.execute(connection, sql, params, preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                BaseDao.closeResource(connection,preparedStatement,null);
            }
        }
        return updateRows;
    }

    //判断输入编码是否与数据中的冲突
    public User userCodeExist(Connection connection, String userCode) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        User user = null;
       if (connection!=null) {
           String sql = "select * from smbms_user where userCode = ?";
           Object[] params = {userCode};
           resultSet = BaseDao.execute(connection, sql, params, resultSet, preparedStatement);
           if (resultSet.next()) {
               user = new User();
               user.setUserCode(resultSet.getString("userCode"));
           }
           BaseDao.closeResource(connection, preparedStatement, resultSet);
       }
        return user;
    }

}
