package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.db.pojo.TbWorkday;

import java.util.List;

/**
 * @Description: WorkdayService
 * @Author Kuky
 * @Date: 2021/6/9 11:51
 * @Version 1.0
 */
public interface WorkdayService {
    void saveBatch(List<TbWorkday> workdays);
}