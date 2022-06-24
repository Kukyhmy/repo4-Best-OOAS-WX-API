package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbDeptDao {
    public ArrayList<HashMap> searchDeptMembers(String keyword);
    public int insertDept(String deptName);

    public int deleteDeptById(int id);

    public int updateDeptById(TbDept entity);

    List<TbDept> searchAllDept();
}
