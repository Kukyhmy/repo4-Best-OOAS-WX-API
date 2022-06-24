package com.kuky.ooas.wx.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: EmailTask
 * @Author Kuky
 * @Date: 2021/6/7 21:28
 * @Version 1.0
 */
@Component
@Scope("prototype")
public class EmailTask implements Serializable {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${emos.email.system}")
    private String mailbox;

    @Async
    public void sendAsync(SimpleMailMessage message){
        message.setFrom(mailbox);
        message.setCc(mailbox);
        javaMailSender.send(message);
    }
}

