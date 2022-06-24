package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbRoleDao {
    public ArrayList<HashMap> searchRoleOwnPermission(int id);
    public ArrayList<HashMap> searchAllPermission();
    public int insertRole(TbRole role);
    public int updateRolePermissions(TbRole role);
    public List<TbRole> searchAllRole();
    public long searchRoleUsersCount(int id);
    public int deleteRoleById(int id);
}