package com.kuky.ooas.wx.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kuky.ooas.wx.db.dao.TbMeetingDao;
import com.kuky.ooas.wx.db.dao.TbUserDao;
import com.kuky.ooas.wx.db.pojo.TbMeeting;
import com.kuky.ooas.wx.exception.EmosException;
import com.kuky.ooas.wx.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: MeetingServiceImpl
 * @Author Kuky
 * @Date: 2021/6/10 15:39
 * @Version 1.0
 */
@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    private TbMeetingDao meetingDao;

    @Autowired
    private TbUserDao tbUserDao;

    @Value("${emos.code}")
    private String code;
    @Value("${workflow.url}")
    private String workflow;

    @Value("${emos.recieveNotify}")
    private String recieveNotify;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void insertMeeting(TbMeeting entity) {
        //保存数据
        int row = meetingDao.insertMeeting(entity);
        if (row != 1) {
            throw new EmosException("会议添加失败");
        }
        // 开启审批工作流
        startMeetingWorkflow(entity.getUuid(), entity.getCreatorId().intValue(), entity.getDate(), entity.getStart());
    }

    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {
        ArrayList<HashMap> list = meetingDao.searchMyMeetingListByPage(param);
        String date = null;
        ArrayList resultList = new ArrayList();
        HashMap resultMap = null;
        JSONArray array = null;
        for (HashMap map : list) {
            String temp = map.get("date").toString();
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap();
                resultList.add(resultMap);
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
            }
            array.put(map);
        }
        return resultList;
    }


    private void startMeetingWorkflow(String uuid, int creatorId, String date, String start) {
        HashMap info = tbUserDao.searchUserInfo(creatorId); //查询创建者用户信息

        JSONObject json = new JSONObject();
        json.set("url", recieveNotify);
        json.set("uuid", uuid);
        json.set("openId", info.get("openId"));
        json.set("code", code);
        json.set("date", date);
        json.set("start", start);
        String[] roles = info.get("roles").toString().split("，");
        //先判断会议创建者是不是总经理  如果不是总经理创建的会议
        if (!ArrayUtil.contains(roles, "总经理")) {
            //查询总经理ID和同部门的经理的ID
            Integer managerId = tbUserDao.searchDeptManagerId(creatorId); //部门经理ID
            json.set("managerId", managerId);
            Integer gmId = tbUserDao.searchGmId();//总经理ID
            json.set("gmId", gmId);
            //查询会议员工是不是同一个部门
            boolean bool = meetingDao.searchMeetingMembersInSameDept(uuid);
            json.set("sameDept", bool);
        }
        String url = workflow + "/workflow/startMeetingProcess";
        //请求工作流接口，开启工作流
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
        if (response.getStatus() == 200) {
            json = JSONUtil.parseObj(response.body());
            //如果工作流创建成功，就更新会议状态
            String instanceId = json.getStr("instanceId");
            HashMap param = new HashMap();
            param.put("uuid", uuid);
            param.put("instanceId", instanceId);
            //在会议记录中保存工作流实例的ID
            int row = meetingDao.updateMeetingInstanceId(param);
            if (row != 1) {
                throw new EmosException("保存会议工作流实例ID失败");
            }
        }
    }


    @Override
    public HashMap searchMeetingById(int id) {
        //会议基本信息
        HashMap map = meetingDao.searchMeetingById(id);
        //会议参会人   u.id,u.name,u.photo
        ArrayList<HashMap> list = meetingDao.searchMeetingMembers(id);
        map.put("members", list);
        return map;
    }

    /**
     * @param param
     * @Description:编辑会议之后，需要删除已有的工作流实例，并且创建新的工作流实例
     * @Author: Kuky
     * @Date: 2022/6/11 1:51
     */
    @Override
    public void updateMeetingInfo(HashMap param) {
        int id = (int) param.get("id");
        String date = param.get("date").toString();
        String start = param.get("start").toString();
        String instanceId = param.get("instanceId").toString();

        //查询修改前的会议记录
        HashMap oldMeeting = meetingDao.searchMeetingById(id);
        String uuid = oldMeeting.get("uuid").toString();
        Integer creatorId = Integer.parseInt(oldMeeting.get("creatorId").toString());
        //更新会议记录
        int row = meetingDao.updateMeetingInfo(param);
        if (row != 1) {
            throw new EmosException("会议更新失败");
        }

        //会议更新成功之后，删除以前的工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason", "会议被修改");
        json.set("uuid", uuid);
        json.set("code", code);
        String url = workflow + "/workflow/deleteProcessById";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
        if (resp.getStatus() != 200) {
            log.error("删除工作流失败");
            throw new EmosException("删除工作流失败");
        }
        //创建新的工作流
        startMeetingWorkflow(uuid, creatorId, date, start);
    }

    @Override
    public void deleteMeetingById(int id) {
        //查询会议信息
        HashMap meeting = meetingDao.searchMeetingById(id);
        String uuid=meeting.get("uuid").toString();
        String instanceId=meeting.get("instanceId").toString();
        DateTime date = DateUtil.parse(meeting.get("date") + " " + meeting.get("start"));
        DateTime now=DateUtil.date();
        //会议开始前20分钟，不能删除会议
        if(now.isAfterOrEquals(date.offset(DateField.MINUTE,-20))){
            throw new EmosException("距离会议开始不足20分钟，不能删除会议");
        };
        int row = meetingDao.deleteMeetingById(id);
        if (row != 1) {
            throw new EmosException("会议删除失败");
        }

        //删除会议工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason", "会议被取消");
        json.set("code",code);
        json.set("uuid",uuid);
        String url = workflow+"/workflow/deleteProcessById";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
        if (resp.getStatus() != 200) {
            log.error("删除工作流失败");
            throw new EmosException("删除工作流失败");
        }
    }


    @Override
    public Long searchRoomIdByUUID(String uuid) {
        Object temp = redisTemplate.opsForValue().get(uuid);
        long roomId = Long.parseLong(temp.toString());
        return roomId;
    }

    @Override
    public List<String> searchUserMeetingInMonth(HashMap param) {
        List list=meetingDao.searchUserMeetingInMonth(param);
        return list;
    }
}
