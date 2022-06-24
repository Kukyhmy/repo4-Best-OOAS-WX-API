package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.db.pojo.TbDept;

import java.util.List;

/**
 * @Description: DeptService
 * @Author Kuky
 * @Date: 2021/6/12 23:32
 * @Version 1.0
 */
public interface DeptService {

    public List<TbDept> searchAllDept();
    public int insertDept(String deptName);
    public void deleteDeptById(int id);
    public void updateDeptById(TbDept entity);
}