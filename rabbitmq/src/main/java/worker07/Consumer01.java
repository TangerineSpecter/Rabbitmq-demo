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
 * 消费者1
 */
public class Consumer01 {

    //普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机
    private static final String DEAD_EXCHANGE = "dead_exchange";

    //普通队列
    private static final String NORMAL_QUEUE = "normal_queue";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明死信和普通交换机类型为 direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明普通队列
        Map<String, Object> arguments = new HashMap<>();
        //过期时间
        //正常队列设置过期死信交换机
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key", "orange");
        //过期时间 10s
//        arguments.put("x-message-ttl", 10 * 1000);
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        System.out.println("等待接收消息......");

        //绑定普通的交换机与队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");
        //绑定死信的交换机和死信的队列
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "orange");
        System.out.println("等待接收消息......");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Consumer01接收的消息是：" + new String(message.getBody(), StandardCharsets.UTF_8));
        };

        channel.basicConsume(NORMAL_QUEUE, true, deliverCallback, consumerTag -> {
        });
    }
}
