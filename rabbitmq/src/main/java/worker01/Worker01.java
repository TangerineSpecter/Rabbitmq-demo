package worker01;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import util.RabbitmqUtils;

/**
 * 工作线程,相当于消费者
 * 演示基本的多个工作线程 进行轮询消息消费
 * 轮询，一个消息只能消费一次，启动Allow parallel run 可以启动多次来达到多个消费者的目的
 */
public class Worker01 {

    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "hello";

    /**
     * 接受消息
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();

        //声明 消息本身有消息头 消息体
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("接受到消息:" + new String(message.getBody()));
        //取消消息时候的回调
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消费消息被中断");
        };
        //消费接收
        System.out.println("C1等待接受消息...");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
