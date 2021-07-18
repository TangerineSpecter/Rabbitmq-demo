package worker03;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import util.RabbitmqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 1.单个确认
 * 2.批量确认
 * 3.异步批量确认
 */
public class ConfirmMessage {

    //批量发消息个数
    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //单个确认 发布1000个单独确认消息,耗时：391ms
        //publishMessageIndividually();

        //批量确认 发布1000个批量确认消息,耗时：44ms
        //publishMessageBatch();

        //异步批量确认 发布1000个异步确认消息,耗时：38ms
        publishMessageAsync();
    }

    /**
     * 单个确认
     */
    private static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long beginTime = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息马上进行确认发布
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        //结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时：" + (endTime - beginTime) + "ms");
    }

    /**
     * 批量确认
     */
    private static void publishMessageBatch() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long beginTime = System.currentTimeMillis();

        //批量确认消息条数
        int batchSize = 100;
        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //判断达到100条消息的时候，批量判断一次
            if (i % batchSize == 0) {
                //发布确认
                channel.waitForConfirms();
            }
        }
        //结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时：" + (endTime - beginTime) + "ms");
    }

    /**
     * 异步确认
     */
    private static void publishMessageAsync() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        /*
         * 线程安全有序的一个哈希表 适用于高并发情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给序号
         * 3.支持高并发
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        //准备消息的监听器，哪些消息成功了，哪些消息失败了。支持监听成功和失败的应答
        //消息确认成功 回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            //判断是否批量，批量就全部进行清除
            if (multiple) {
                //删除已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            } else {
                System.out.println("进行消息确认移除，标记" + deliveryTag);
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认消息：" + deliveryTag);
        };
        //消息确认失败 回调函数 1.消息标记，2.是否为批量确认
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            //3.打印一下未确认的消息
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认消息：" + message);
        };
        /*
         * 1.监听哪些消息成功
         * 2.监听哪些消息失败
         */
        channel.addConfirmListener(ackCallback, nackCallback);

        //开始时间
        long beginTime = System.currentTimeMillis();
        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            //1.记录下所有要发送消息的总和  key:序号，value：消息 发布前使用
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        System.out.println(outstandingConfirms);
        //结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时：" + (endTime - beginTime) + "ms");
    }
}
