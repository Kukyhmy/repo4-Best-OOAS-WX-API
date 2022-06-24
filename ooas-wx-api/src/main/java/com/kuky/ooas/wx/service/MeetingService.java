package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: MeetingService
 * @Author Kuky
 * @Date: 2021/6/10 15:39
 * @Version 1.0
 */
public interface MeetingService {
    public void insertMeeting(TbMeeting entity);

    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    public HashMap searchMeetingById(int id);


    public void updateMeetingInfo(HashMap param);


    public void deleteMeetingById(int id);

    public Long searchRoomIdByUUID(String uuid);

    public List<String> searchUserMeetingInMonth(HashMap param);
}
