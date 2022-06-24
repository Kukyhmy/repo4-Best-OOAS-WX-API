package com.kuky.ooas.wx.service.impl;

import cn.hutool.json.JSONObject;
import com.kuky.ooas.wx.db.dao.TbRoleDao;
import com.kuky.ooas.wx.db.pojo.TbRole;
import com.kuky.ooas.wx.exception.EmosException;
import com.kuky.ooas.wx.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @Description: RoleServiceImpl
 * @Author Kuky
 * @Date: 2021/6/12 23:08
 * @Version 1.0
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private TbRoleDao roleDao;

    @Override
    public ArrayList<HashMap> searchRoleOwnPermission(int id) {
        ArrayList<HashMap> list = roleDao.searchRoleOwnPermission(id);
        list = handleData(list);
        return list;
    }

    /**
     * 将查询结果按照模块名称分组
     *
     * @param list
     * @return
     */
    private ArrayList<HashMap> handleData(ArrayList<HashMap> list) {
        ArrayList permsList = new ArrayList();
        ArrayList actionList = new ArrayList();
        HashSet set = new HashSet();
        HashMap data = new HashMap();

        for (HashMap map : list) {
            long permissionId = (Long) map.get("id");
            String moduleName = (String) map.get("moduleName");
            String actionName = (String) map.get("actionName");
            String selected = map.get("selected").toString();

            if (set.contains(moduleName)) {
                JSONObject json = new JSONObject();
                json.set("id", permissionId);
                json.set("actionName", actionName);
                json.set("selected", selected.equals("1") ? true : false);
                actionList.add(json);
            } else {
                set.add(moduleName);
                data = new HashMap();
                data.put("moduleName", moduleName);
                actionList = new ArrayList();
                JSONObject json = new JSONObject();
                json.set("id", permissionId);
                json.set("actionName", actionName);
                json.set("selected", selected.equals("1") ? true : false);
                actionList.add(json);
                data.put("action", actionList);
                permsList.add(data);
            }

        }
        return permsList;
    }


    @Override
    public ArrayList<HashMap> searchAllPermission(){
        ArrayList<HashMap> list = roleDao.searchAllPermission();
        list=handleData(list);
        return list;
    }

    @Override
    public void insertRole(TbRole role) {
        int row = roleDao.insertRole(role);
        if (row != 1) {
            throw new EmosException("添加角色失败");
        }
    }

    @Override
    public void updateRolePermissions(TbRole role) {
        int row = roleDao.updateRolePermissions(role);
        if (row != 1) {
            throw new EmosException("修改角色失败");
        }
    }

    @Override
    public List<TbRole> searchAllRole() {
        List<TbRole> list = roleDao.searchAllRole();
        return list;
    }

    @Override
    public void deleteRoleById(int id) {
        long count = roleDao.searchRoleUsersCount(id);
        if (count > 0) {
            throw new EmosException("该角色关联着用户，所以无法删除");
        }
        int row = roleDao.deleteRoleById(id);
        if (row != 1) {
            throw new EmosException("角色删除失败");
        }
    }
}
