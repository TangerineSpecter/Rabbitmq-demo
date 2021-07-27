package com.specter.rabbitmq.springbootrabbitmq.config;

import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列插件配置
 */
@Slf4j
@Configuration
public class DelayedQueueConfig {

    /**
     * 交换机名称
     */
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    /**
     * 队列名称
     */
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    /**
     * routingKey
     */
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    @Bean("delayedQueue")
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    /**
     * 声明交换机
     */
    @Bean("delayedExchange")
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = new HashMap<>(2);
        arguments.put("x-delayed-type", BuiltinExchangeType.DIRECT);
        /*
         * 自定义交换机
         * 1.交换机的名称
         * 2.交换机的类型
         * 3.是否需要持久化
         * 4.是否需要自动删除
         * 5.其他参数
         */
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, arguments);
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding delayedQueueBindingDelayedExchange(@Qualifier("delayedQueue") Queue delayedQueue,
                                                      @Qualifier("delayedExchange") CustomExchange delayedExchange) {
        log.info("进行交换机{}和队列{}绑定", delayedExchange, delayedQueue);
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }
}
