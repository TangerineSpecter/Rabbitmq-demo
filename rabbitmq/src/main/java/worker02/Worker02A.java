package worker02;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import util.RabbitmqUtils;
import util.SleepUtils;

import java.nio.charset.StandardCharsets;

/**
 * 消息在手动应答时不丢失，放回队列中重新消费
 */
public class Worker02A {

    /**
     * 队列名称
     */
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        System.out.println("C1等待接收消息处理时间较短");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            //沉睡1秒
            SleepUtils.sleep(1);
            System.out.println("接收到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
            /*
             * 手动应答
             * 1.消息标记 tag
             * 2.是否批量应答 false:不批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        //设置不公平分发 0：轮询；1：不公平
        channel.basicQos(1);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        });
    }
}
