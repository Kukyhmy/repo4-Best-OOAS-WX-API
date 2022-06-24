package com.kuky.ooas.wx.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.kuky.ooas.wx.db.pojo.MessageEntity;
import com.kuky.ooas.wx.db.pojo.MessageRefEntity;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: MessageDao
 * @Author Kuky
 * @Date: 2021/6/9 19:41
 * @Version 1.0
 */
@Repository
@Slf4j
public class MessageDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageEntity entity) {
        //把北京时间转换成格林尼治时间
        Date sendTime = entity.getSendTime();
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        entity.setSendTime(sendTime);
        entity = mongoTemplate.save(entity);
        return entity.get_id();
    }

    /*
     * @Description:分页查询数据
     * 集合连接对象Aggregation，专门做集合查询
     * @param userId
     * @param start 起始位置 类型必须为long
     * @param length 偏移量
     * @return: java.util.List<java.util.HashMap>
     * @Author: Kuky
     * @Date: 2022/6/9 19:44
     */
    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        JSONObject json = new JSONObject();
        json.set("$toString", "$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(json).build(),
                Aggregation.lookup("message_ref", "id", "messageId", "ref"),
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        List<HashMap> list = results.getMappedResults();
        list.forEach(one -> {
            List<MessageRefEntity> refList = (List<MessageRefEntity>) one.get("ref");
//            报错：java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class com.kuky.ooas.wx.db.pojo.MessageRefEntity (java.util.LinkedHashMap is in module java.base of loader 'bootstrap'; com.kuky.ooas.wx.db.pojo.MessageRefEntity is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @332c42cd)
            log.info("refList.get(0)-----:"+refList.get(0));
            MessageRefEntity entity = refList.get(0);

            boolean readFlag = entity.getReadFlag();
            String refId = entity.get_id();
            one.remove("ref");
            one.put("readFlag", readFlag);
            one.put("refId", refId);
            one.remove("_id");
            //把格林尼治时间转换成北京时间
            Date sendTime = (Date) one.get("sendTime");
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);


            String today = DateUtil.today();
            //如果是今天的消息，只显示发送时间，不需要显示日期
            if (today.equals(DateUtil.date(sendTime).toDateStr())) {
                one.put("sendTime", DateUtil.format(sendTime, "HH:mm"));
            }
            //如果是以往的消息，只显示日期，不显示发送时间
            else {
                one.put("sendTime", DateUtil.format(sendTime, "yyyy/MM/dd"));
            }
        });
        return list;
    }

    public HashMap searchMessageById(String id) {
        HashMap map = mongoTemplate.findById(id, HashMap.class, "message");
        Date sendTime = (Date) map.get("sendTime");
        //把格林尼治时间转换成北京时间
        sendTime = DateUtil.date(sendTime).offset(DateField.HOUR, -8);
        map.replace("sendTime", DateUtil.format(sendTime, "yyyy-MM-dd HH:mm"));
        return map;
    }

    public long deleteUserMessage(int receiverId){
        Query query=new Query();
        query.addCriteria(Criteria.where("receiverId").is(receiverId));
        DeleteResult result=mongoTemplate.remove(query,"message");
        long rows=result.getDeletedCount();
        return rows;
    }
}
