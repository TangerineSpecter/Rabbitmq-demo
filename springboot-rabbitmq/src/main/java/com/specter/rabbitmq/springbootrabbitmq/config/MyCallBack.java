package com.specter.rabbitmq.springbootrabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 消息确认回调、消息回退接口
 * 实现RabbitTemplate内部接口
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    /**
     * RabbitTemplate.ConfirmCallback是一个内部接口
     * MyCallBack这个实现类并不在RabbitTemplate里面
     * 会导致RabbitTemplate调用自身接口的时候调用不到MyCallBack这个实现类
     * 所以需要把MyCallBack注入到RabbitTemplate里面的ConfirmCallback接口里
     * 执行顺序@Component->@Autowired->@PostConstruct
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 交换机确认回调方法
     *
     * @param correlationData 保存回调消息的ID以及相关信息，需要发消息的时候自己填入
     * @param ack             交换机收到的消息 true：成功；false：失败
     * @param cause           失败的原因，如果没有失败，返回null
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData.getId() != null ? correlationData.getId() : "";
        if (ack) {
            log.info("交换机已经收到ID为：{}的消息", id);
        } else {
            log.info("交换机还未收到ID为：{}的消息，原因：{}", id, cause);
        }
    }

    /**
     * 当消息传递过程中不可达目的地时将消息返回给生产者
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息{}，被交换机{}退回，退回原因：{}，路由Key{}", new String(message.getBody()), exchange, replyText, routingKey);
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {

    }
}
