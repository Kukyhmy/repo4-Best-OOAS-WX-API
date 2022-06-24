package com.kuky.ooas.wx.service.impl;

import com.kuky.ooas.wx.db.dao.TbDeptDao;
import com.kuky.ooas.wx.db.dao.TbUserDao;
import com.kuky.ooas.wx.db.pojo.TbDept;
import com.kuky.ooas.wx.exception.EmosException;
import com.kuky.ooas.wx.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: DeptServiceImpl
 * @Author Kuky
 * @Date: 2021/6/12 23:33
 * @Version 1.0
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private TbDeptDao deptDao;

    @Autowired
    private TbUserDao userDao;

    @Override
    public List<TbDept> searchAllDept() {
        List<TbDept> list = deptDao.searchAllDept();
        return list;
    }

    @Override
    public int insertDept(String deptName) {
        int row = deptDao.insertDept(deptName);
        if (row != 1) {
            throw new EmosException("部门添加失败");
        }
        return row;
    }

    @Override
    public void deleteDeptById(int id) {
        //查询部门是否有数据
        long count = userDao.searchUserCountInDept(id);
        if (count > 0) {
            throw new EmosException("部门中有员工，无法删除部门");
        } else {
            int row = deptDao.deleteDeptById(id);
            if (row != 1) {
                throw new EmosException("部门删除失败");
            }
        }
    }

    @Override
    public void updateDeptById(TbDept entity){
        int row=deptDao.updateDeptById(entity);
        if (row != 1) {
            throw new EmosException("部门删除失败");
        }
    }
}
