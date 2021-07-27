package com.specter.rabbitmq.springbootrabbitmq.consumer;

import com.specter.rabbitmq.springbootrabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 接受消息
 */
@Slf4j
@Component
public class Consumer {

    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message) {
        System.out.println(new String(message.getBody()));
        log.info("接收到的队列confirm.queue消息：{}", message);
    }

}
