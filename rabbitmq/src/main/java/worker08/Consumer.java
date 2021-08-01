package worker08;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import util.RabbitmqUtils;

/**
 * 消费者
 */
public class Consumer {

    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitmqUtils.getChannel();

        //声明 消息本身有消息头 消息体
        DeliverCallback deliverCallback = (consumerTag, message) -> System.out.println("接受到消息:" + new String(message.getBody()));
        //取消消息时候的回调
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消费消息被中断");
        };
        /*
         * 消费消息
         * 1.消费队列名称
         * 2.消费成功之后是否需要自动应答 true 自动应答
         * 3.消费未成功的回调
         * 4.消费者录取消费的回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
