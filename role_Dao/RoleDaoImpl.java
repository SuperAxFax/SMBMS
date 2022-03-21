package com.kuang.Dao.role;

import com.kuang.Dao.BaseDao;
import com.kuang.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao {
        //获取角色列表
        public List<Role> getRoleList(Connection connection) throws SQLException {
            ArrayList<Role> roleList = new ArrayList<Role>();
            ResultSet resultSet = null;
            PreparedStatement pstm = null;


            if (connection!=null){
                String sql = "select * from smbms_role";
                Object[] params = {};
                resultSet = BaseDao.execute(connection, sql, params, resultSet, pstm);
                while(resultSet.next()){
                    Role role = new Role();
                    role.setId(resultSet.getInt("id"));
                    role.setRoleCode(resultSet.getString("roleCode"));
                    role.setRoleName(resultSet.getString("roleName"));
                    roleList.add(role);
                }
                BaseDao.closeResource(connection,pstm,resultSet);
            }
            return roleList;
        }

}
