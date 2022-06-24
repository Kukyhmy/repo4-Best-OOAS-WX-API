package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {
    public String searchFaceModel(int userId);

    public void insert(TbFaceModel faceModelEntity);
    public int deleteFaceModel(int userId);
}
