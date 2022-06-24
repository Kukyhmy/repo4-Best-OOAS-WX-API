package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbCheckinDao {
    public Integer haveCheckin(HashMap param);

    public void insert(TbCheckin entity);

    public HashMap searchTodayCheckin(int userId);

    public long searchCheckinDays(int userId);

    public ArrayList<HashMap> searchWeekCheckin(HashMap param);

    public int deleteUserCheckin(int userId);
}
