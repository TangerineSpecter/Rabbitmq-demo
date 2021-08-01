package worker08;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import util.RabbitmqUtils;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> arguments = new HashMap<>();
        //优先级0~255，这里设置10，允许优先级范围0~10，设置过大会浪费CPU和内存
        arguments.put("x-max-priority", 10);
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);

        for (int index = 1; index <= 10; index++) {
            String message = "info" + index;
            if (index == 5) {
                //设置优先级5，消费者打印结果 info5 最前面，数字越大优先级越高，不设置的优先级最低
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();

                channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
            } else {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }
        }
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
