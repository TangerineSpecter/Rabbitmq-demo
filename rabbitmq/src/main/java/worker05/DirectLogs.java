package worker05;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import util.RabbitmqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * direct 直接推送消息
 */
public class DirectLogs {

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发送消息：" + message);
        }
    }
}
