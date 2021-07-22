package worker07;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import util.RabbitmqUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列 实战
 * 死信消费者2
 */
public class Consumer02 {

    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        System.out.println("等待接收消息......");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Consumer02接收的死信消息是：" + new String(message.getBody(), StandardCharsets.UTF_8));
        };

        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, consumerTag -> {
        });
    }
}
