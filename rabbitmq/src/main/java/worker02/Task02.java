package worker02;

import com.rabbitmq.client.Channel;
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
        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
        //从控制台输入信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + message);
        }
    }
}
