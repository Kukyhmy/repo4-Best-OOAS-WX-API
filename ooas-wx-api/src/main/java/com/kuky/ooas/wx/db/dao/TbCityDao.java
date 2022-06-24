package com.kuky.ooas.wx.db.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCityDao {
    public String searchCode(String city);
}
