package startdemo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import util.RabbitmqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class Producer {

    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "hello";

    /**
     * 发消息
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        Channel channel = RabbitmqUtils.getChannel();
        /**
         * 创建队列
         * 1.队列名称
         * 2.队列消息是否持久化，默认存储在内存中
         * 3.该队列是否只提供一个消费者进行消费，是否进行消息共享,true支持多个
         * 4.是否自动删除，最后一个消费者断开连接以后，该队列是否自动删除，true自动删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //发消息
        String message = "hello world";
        /**
         * 1.发送到哪个交换机
         * 2.路由key值是哪个
         * 3.其他参数
         * 4.消息
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
    }
}
