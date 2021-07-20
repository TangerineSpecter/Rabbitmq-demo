package worker04;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import util.RabbitmqUtils;

import java.nio.charset.StandardCharsets;

/**
 * 消息接收
 */
public class ReceiveLogs02 {
    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        /*
         * 声明一个队列 临时队列
         * 队列名称随机
         * 消费者断开连接与队列的连接的时候，队列就自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        /*
         * 绑定交换机与队列
         */
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息，把接收消息打印在屏幕上");

        //消息确认成功 回调函数
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogs02控制台打印接收到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
