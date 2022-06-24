package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbWorkdayDao {
    public Integer searchTodayIsWorkday();
    public ArrayList<String> searchWorkdayInRange(HashMap param);

    void saveBatch(List<TbWorkday> workdaysList);
}
