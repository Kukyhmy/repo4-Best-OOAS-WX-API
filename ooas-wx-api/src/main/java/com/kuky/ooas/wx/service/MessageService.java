package com.kuky.ooas.wx.service;

import com.kuky.ooas.wx.db.pojo.MessageEntity;
import com.kuky.ooas.wx.db.pojo.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: MessageService
 * @Author Kuky
 * @Date: 2021/6/9 22:00
 * @Version 1.0
 */
public interface MessageService {

    public String insertMessage(MessageEntity entity);

    public String insertRef(MessageRefEntity entity);

    public long searchUnreadCount(int userId);

    public long searchLastCount(int userId);

    public List<HashMap> searchMessageByPage(int userId, long start, int length) ;

    public HashMap searchMessageById(String id);

    public long updateUnreadMessage(String id) ;

    public long deleteMessageRefById(String id);

    public long deleteUserMessageRef(int userId);
}
