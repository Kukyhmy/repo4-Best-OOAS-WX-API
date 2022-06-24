package com.kuky.ooas.wx.service.impl;

import com.kuky.ooas.wx.db.dao.TbWorkdayDao;
import com.kuky.ooas.wx.db.pojo.TbWorkday;
import com.kuky.ooas.wx.service.WorkdayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: WorkdayServiceImpl
 * @Author Kuky
 * @Date: 2021/6/9 11:52
 * @Version 1.0
 */
@Service
@Slf4j
public class WorkdayServiceImpl implements WorkdayService {
    @Autowired
    private TbWorkdayDao workdayDao;
    @Override
    public void saveBatch(List<TbWorkday> workdays) {
        workdayDao.saveBatch(workdays);
    }
}