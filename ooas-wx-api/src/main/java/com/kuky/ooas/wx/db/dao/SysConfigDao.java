package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysConfigDao {
    public List<SysConfig> selectAllParam();
}
