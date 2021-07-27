# Rabbitmq-demo
raabbitmq消息队列演示

## MQ概念

接收存储转发消息

### MQ功能

````
1.流量削峰：消息队列缓冲，调用人员排队
2.应用解耦
3.异步处理
````

### MQ的四大核心功能
````
1.生产者 Producter：负责生产消息
2.交换机 Exchange：一个交换机可以拥有多个队列，可以存在多个交换机
3.队列 Queue：和交换机进行绑定，存储消息
4.消费者 Consumer：进行消息消费
````

### 消息信道和Connection

连接大量Connection会消耗大量资源，所以Channel是在conn内部建立的逻辑连接

### 代码演示

- startdemo：基本的消息发送接收演示
- worker01：工作线程演示，多个消费者消费消息
- worker02：工作线程手动应答、重新入队、消息持久化演示      
- worker03：消息发布确认的三个方式，单个、批量、异步发布确认
- worker04：fanout扇型交换机演示
- worker05：direct直接交换机演示
- worker06：topic主题交换机演示
- worker07：死信队列演示

---

### 笔记   

- 不公平分发：能者多劳，处理快的消费者，消费消息更多
- 预取值：信道没满的有机会继续消费消息，满了分配给信道没达到预取值的消费者
- 发布确认：（满足三点，才能绝对保证消息不丢失）
    * 1.队列持久化；
    * 2.消息持久化；
    * 3.发布确认。
- 发布确认策略：
    * 1.单个确认：同步发送，只有一个消息确认了，才会发下一条。缺陷是发送速度特别慢。
    * 2.多个确认：先发布一批消息，然后一起确认，可以极大的提高吞吐量，缺陷是发生故障，不知道是哪个消息出现问题。    
    * 3异步确认：性价比最高，无论可靠性和效率都不错，利用回调函数来达到消息的可靠性传递。map容器记录消息序号和内容，可以确认失败的消息。
- 交换机（Exchange)
    * 生产者只能将消息发送到交换机
- 交换机类型
````
1.直接（direct）：当消息发送到直接交换机上，会根据绑定队列的routingkey推送匹配的消息。
2.主题（topic）：将路由键和某模式进行匹配，符号"#"表示一个或者多个词，符号"*"表示一个词。routingkey必须是一个单词。
3.标题（headers）：通过键值对匹配的形式发送消息，不依赖routingKey。all表示多个键值对都要满足，any表示只需要满足其中一个。
4.扇出（fanout）：当消息发送到扇形交换机上，交换机会将消息路由给所有绑定这个交换机的队列上。
````

- 临时队列
    * 一旦断开消费者连接，队列将被自动删除
    * 创建方式：channel.queueDeclare().getQueue();
- binding
    * exchange和queue之间的桥梁，能根据routingKey决定和哪个队列绑定
- 死信队列
    * 无法被消费的消息
    * 应用于订单业务消息数据不丢失，当消息异常的时候，将消息投入死信队列
    * 来源：
        * 消息TTL过期
        * 队列达到最大长度
        * 消息被拒绝
- 延迟队列
    * 延迟队列内部是有序的
    * 队列用来存放指定时间被处理的元素队列
    * 使用场景：
        * 订单10分钟内未支付，则自动取消
        * 用户注册，3天内没登录进行短信提醒
        * 用户发起退款，三天内没处理通知相关运营人员
        * 预定会议，开始时间前10分钟通知各个会议人员
    * 相较于使用定时任务检测，对于数据量比较大，并且时效性很强的场景，轮询是不可取的。所以延迟队列就有用了。
    * 通过安装插件处理延迟队列排队等待问题
    
- 延迟消息插件安装：
````
下载地址：
https://www.rabbitmq.com/community-plugins.html

下载插件：
rabbitmq_delayed_message_exchange

复制插件到RabbitMQ目录下的plugins文件夹中：
/usr/local/Cellar/rabbitmq/3.7.16/plugins

安装插件并重启MQ：（提示started 1 plugins 表示安装成功） 
rabbitmq-plugins enable rabbitmq_delayed_message_exchange

登录后台可看见新增了一个x-delayed-message的交换机。  
这样就把之前基于死信队列的消息延迟，变成了基于交换机的延迟。
````

- 发布确认（避免rabbitMQ重启或者宕机导致消息投递失败、丢失）
    - 交换机、队列其中一个不存在就会丢失
    - 通过实现RabbitTemplate.ConfirmCallback进行交换机失败回调处理
    - 通过实现RabbitTemplate.ReturnsCallback进行队列失败回调处理
    
- 备份交换机
    - 可以让交换机不回退消息给生产者
    - 无法投递消息的时候发送给备份交换机
    - 可以用于监控和报警 