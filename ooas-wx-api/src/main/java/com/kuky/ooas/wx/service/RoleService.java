package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.db.pojo.TbRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: RoleService
 * @Author Kuky
 * @Date: 2021/6/12 23:07
 * @Version 1.0
 */
public interface RoleService {
    public ArrayList<HashMap> searchRoleOwnPermission(int id);
    public ArrayList<HashMap> searchAllPermission();
    public void insertRole(TbRole role);
    public void updateRolePermissions(TbRole role);
    public List<TbRole> searchAllRole();
    public void deleteRoleById(int id);
}