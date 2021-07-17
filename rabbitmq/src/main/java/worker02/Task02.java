package worker02;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import util.RabbitmqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 消息在手动应答时是不丢失、放回队列中重新消费
 */
public class Task02 {

    /**
     * 队列名称
     */
    private static final String TASK_QUEUE_NAME = "ack_queue";

    /**
     * 启动Task02, 再启动Worker02A、Worker02B，消息睡眠唤醒消费之前，被中断异常，由另外一个消费者消费
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明队列
        //需要队列持久化，改持久化一定要删除之前的队列，进行重新创建，否则会报错。可以到控制台删除之前队列
        boolean durable = true;
        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
        //从控制台输入信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            //消息持久化
            //MessageProperties.PERSISTENT_TEXT_PLAIN 消息持久化（保存在硬盘上）保存在内存中，
            // 不保证一定不丢失，可能还没保存完，消息还在缓存的一个间隔点，此时并没有真正写入磁盘
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + message);
        }
    }
}
