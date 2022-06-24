package com.kuky.ooas.wx.db.dao;

import com.kuky.ooas.wx.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbMeetingDao {
    public int insertMeeting(TbMeeting entity);

    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    public boolean searchMeetingMembersInSameDept(String uuid);
    public int updateMeetingInstanceId(HashMap map);

    public HashMap searchMeetingById(int id);
    public ArrayList<HashMap> searchMeetingMembers(int id);

    public int updateMeetingInfo(HashMap param);

    public int deleteMeetingById(int id);
    public List<String> searchUserMeetingInMonth(HashMap param);
}
