package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.db.pojo.TbHolidays;

import java.util.List;

/**
 * @Description: HolidaysService
 * @Author Kuky
 * @Date: 2021/6/9 11:51
 * @Version 1.0
 */
public interface HolidaysService {
    void saveBatch(List<TbHolidays> holidays);
}