package com.kuky.ooas.wx.service.impl;

import com.kuky.ooas.wx.db.dao.TbHolidaysDao;
import com.kuky.ooas.wx.db.pojo.TbHolidays;
import com.kuky.ooas.wx.service.HolidaysService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: HolidaysServiceImpl
 * @Author Kuky
 * @Date: 2021/6/9 11:51
 * @Version 1.0
 */
@Service
@Slf4j
public class HolidaysServiceImpl implements HolidaysService {
    @Autowired
    private TbHolidaysDao holidaysDao;
    @Override
    public void saveBatch(List<TbHolidays> holidays) {
        holidaysDao.saveBatch( holidays);
    }
}