package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbHolidaysDao {
    public Integer searchTodayIsHolidays();
    public ArrayList<String> searchHolidaysInRange(HashMap param);

    void saveBatch(List<TbHolidays> holidaysList);
}
