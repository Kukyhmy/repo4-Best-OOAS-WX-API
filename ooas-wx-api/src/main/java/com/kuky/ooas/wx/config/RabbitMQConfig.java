package com.kuky.ooas.wx.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: RabbitMQConfig
 * @Author Kuky
 * @Date: 2021/6/9 22:29
 * @Version 1.0
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("172.20.10.7"); //Linux主机的IP地址
        factory.setPort(5672); //RabbitMQ端口号
        return factory;
    }
}

