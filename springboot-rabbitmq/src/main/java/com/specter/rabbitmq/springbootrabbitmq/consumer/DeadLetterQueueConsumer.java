package com.specter.rabbitmq.springbootrabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 延迟队列TTL 消费者
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    /**
     * 监听死信队列QD 接收消息
     */
    @RabbitListener(queues = "QD")
    public void receiveD(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("当前时间：{},收到死信消息队列：{}", new Date().toString(), msg);
    }
}
