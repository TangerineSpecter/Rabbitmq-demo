package worker04;

import com.rabbitmq.client.Channel;
import util.RabbitmqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 发消息 交换机
 * fanout 扇形交换机
 */
public class EmitLog {

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发送消息：" + message);
        }
    }
}
