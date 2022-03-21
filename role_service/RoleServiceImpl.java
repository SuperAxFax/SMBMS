package com.kuang.service.role;

import com.kuang.Dao.BaseDao;
import com.kuang.Dao.role.RoleDao;
import com.kuang.Dao.role.RoleDaoImpl;
import com.kuang.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoleServiceImpl implements RoleService {
    private RoleDao roleDao;

    //如果调用这个类，进来就会执行无参构造中的内容！
    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    public List<Role> getRoleList() {
        //引入RoleDao
        Connection connection = null;
        List<Role> roleList = null;
        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roleList;
    }

    @Test
    public void Test(){
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}
