package worker07;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import util.RabbitmqUtils;

/**
 * 死信队列 生产者
 * 启动消费者1，关闭模拟死信，然后启动生产者，启动死信消费者2.消费死信
 */
public class Producer {

    //普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //死信消息，设置TTL时间
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i <= 10; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE,   "zhangsan", properties, message.getBytes());
            System.out.println("发送消息：" + message);
        }
    }

}
