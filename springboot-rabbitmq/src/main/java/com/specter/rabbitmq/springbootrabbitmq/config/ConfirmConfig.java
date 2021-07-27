package com.specter.rabbitmq.springbootrabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息发布确认配置类
 */
@Slf4j
@Configuration
public class ConfirmConfig {

    /**
     * 交换机
     */
    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";
    /**
     * 队列
     */
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    /**
     * RoutingKey
     */
    public static final String CONFIRM_ROUTING_KEY = "confirm_key";
    /**
     * 备份交换机
     */
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    /**
     * 备份队列
     */
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
    /**
     * 报警队列
     */
    public static final String WARNING_QUEUE_NAME = "warning_queue";

    /**
     * 声明交换机
     */
    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        //配置转发给备份交换机 alternate-exchange
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME).durable(true)
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME).build();
    }

    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding queueBindingExchange(@Qualifier("confirmQueue") Queue confirmQueue,
                                        @Qualifier("confirmExchange") DirectExchange confirmExchange) {
        log.info("进行交换机{}和队列{}绑定", confirmQueue, confirmExchange);
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }

    /**
     * 备份交换机
     */
    @Bean
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    /**
     * 备份队列
     */
    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    /**
     * 报警队列
     */
    @Bean
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    /**
     * 备份队列绑定
     */
    @Bean
    public Binding backupQueueBindingBackupExchange(@Qualifier("backupQueue") Queue backupQueue,
                                                    @Qualifier("backupExchange") FanoutExchange backupExchange) {
        log.info("进行备份交换机{}和备份队列{}绑定", backupQueue, backupExchange);
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    /**
     * 报警队列绑定
     */
    @Bean
    public Binding warningQueueBindingBackupExchange(@Qualifier("warningQueue") Queue warningQueue,
                                                     @Qualifier("backupExchange") FanoutExchange backupExchange) {
        log.info("进行备份交换机{}和报警队列{}绑定", warningQueue, backupExchange);
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }
}
