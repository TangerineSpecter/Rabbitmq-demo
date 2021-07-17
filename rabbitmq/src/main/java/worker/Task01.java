package worker;

import com.rabbitmq.client.Channel;
import util.RabbitmqUtils;

import java.util.Scanner;

/**
 * 生产者 负责发送大量消息
 */
public class Task01 {

    /**
     * 队列名称
     */
    public static final String QUEUE_NAME = "hello";

    //发送大量消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //队列的声明
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //从控制台中接受消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息完成：" + message);
        }
    }
}
