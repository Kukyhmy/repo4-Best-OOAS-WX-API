package com.kuky.ooas.wx.db.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: MessageEntity
 * @Author Kuky
 * @Date: 2022/6/9 17:09
 * @Version 1.0
 */
@Data
@Document(collection = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;

    @Indexed(unique = true)
    private String uuid;

    @Indexed
    private Integer senderId;

    private String senderPhoto="https://static-1258386385.cos.ap-beijing.myqcloud.com/img/System.jpg";

    private String senderName;

    private String msg;

    @Indexed
    private Date sendTime;
}
