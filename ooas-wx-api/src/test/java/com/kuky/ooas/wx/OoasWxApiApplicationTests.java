package com.kuky.ooas.wx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.kuky.ooas.wx.db.pojo.MessageEntity;
import com.kuky.ooas.wx.db.pojo.MessageRefEntity;
import com.kuky.ooas.wx.db.pojo.TbMeeting;
import com.kuky.ooas.wx.service.MeetingService;
import com.kuky.ooas.wx.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class OoasWxApiApplicationTests {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MeetingService meetingService;

    @Test
    void contextLoads() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id = messageService.insertMessage(message);

            MessageRefEntity ref = new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(21); //注意：这是接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }

    @Test
    void createMeetingData() {
        for (int i = 1; i <= 100; i++) {
            TbMeeting meeting = new TbMeeting();
            meeting.setId((long) i);
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("测试会议" + i);
            meeting.setCreatorId(21L); //ROOT用户ID
            meeting.setDate(DateUtil.today());
            meeting.setPlace("线上会议室");
            meeting.setStart("08:30");
            meeting.setEnd("10:30");
            meeting.setType((short) 1);
            meeting.setMembers("[21,16]");
            meeting.setDesc("会议研讨Emos项目上线测试");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus((short) 3);
            meeting.setCreateTime(new Date());
            meetingService.insertMeeting(meeting);
        }
    }
}
