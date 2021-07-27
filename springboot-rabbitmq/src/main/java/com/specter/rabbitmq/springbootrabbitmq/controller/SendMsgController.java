package com.specter.rabbitmq.springbootrabbitmq.controller;

import com.specter.rabbitmq.springbootrabbitmq.config.ConfirmConfig;
import com.specter.rabbitmq.springbootrabbitmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 返送延迟消息
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private String message;

    /**
     * 消息推送接口
     *
     * @param message 推送消息
     */
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message) {
        log.info("当前时间：{},发送一条信息给两个TTL队列：{}", new Date().toString(), message);
        //进行消息推送
        rabbitTemplate.convertAndSend("X", "XA", "消息来自ttl为10s的队列：" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自ttl为40s的队列：" + message);
    }

    /**
     * 自定义时间消息推送接口
     */
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message, @PathVariable String ttlTime) {
        log.info("当前时间：{},发送一条时长{}毫秒的信息给一个TTL队列：{}", new Date().toString(), ttlTime, message);
        //进行消息推送
        rabbitTemplate.convertAndSend("X", "XC", "消息来自ttl为" + ttlTime + "ms的队列：" + message, msg -> {
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }

    /**
     * 基于插件发送延迟消息
     * 需要安装插件：rabbitmq_delayed_message_exchange ，安装参照ReadME
     */
            @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message, @PathVariable Integer delayTime) {
        log.info("当前时间：{},发送一条时长{}毫秒的信息给一个延迟队列delayed.queue：{}" , new Date().toString(), delayTime, message);
        //进行消息推送 延迟时长，单位ms
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME, DelayedQueueConfig.DELAYED_ROUTING_KEY, message, msg -> {
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        });
    }

    /**
     * 发消息 测试消息确认
     */
    @GetMapping("/sendConfirmMsg/{message}")
    public void sendConfirmMsg(@PathVariable String message) {
        CorrelationData correlationData = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY, message, correlationData);
        log.info("发送确认消息内容：{}", message);
    }
}
