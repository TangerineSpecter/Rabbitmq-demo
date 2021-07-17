# Rabbitmq-demo
raabbitmq消息队列演示

## MQ概念

接收存储转发消息

### MQ功能

1.流量削峰：消息队列缓冲，调用人员排队
2.应用解耦
3.异步处理

### MQ的四大核心功能

1.生产者 Producter：负责生产消息
2.交换机 Exchange：一个交换机可以拥有多个队列，可以存在多个交换机
3.队列 Queue：和交换机进行绑定，存储消息
4.消费者 Consumer：进行消息消费

### 消息信道和Connection

连接大量Connection会消耗大量资源，所以Channel是在conn内部建立的逻辑连接

### 代码演示

startdemo：基本的消息发送接收演示
worker01：工作线程演示，多个消费者消费消息
worker02：工作线程手动应答以及重新入队演示         