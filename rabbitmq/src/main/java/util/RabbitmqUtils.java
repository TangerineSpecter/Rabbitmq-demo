package util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 工具类
 */
public class RabbitmqUtils {

    public static Channel getChannel() throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP
        factory.setHost("127.0.0.1");
        //用户名
        factory.setUsername("guest");
        //密码
        factory.setPassword("guest");
        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        return connection.createChannel();
    }
}
