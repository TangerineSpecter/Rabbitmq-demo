# Rabbitmq-demo

RaabbitMq消息队列演示

#### RabbitMQ概念

接收存储转发消息

#### RabbitMQ功能

````
1.流量削峰：消息队列缓冲，调用人员排队
2.应用解耦
3.异步处理
````

#### RabbitMQ的四大核心功能
````
1.生产者 Producter：负责生产消息
2.交换机 Exchange：一个交换机可以拥有多个队列，可以存在多个交换机
3.队列 Queue：和交换机进行绑定，存储消息
4.消费者 Consumer：进行消息消费
````

#### 消息信道和Connection

连接大量Connection会消耗大量资源，所以Channel是在conn内部建立的逻辑连接

#### 代码演示

- startdemo：基本的消息发送接收演示
- worker01：工作线程演示，多个消费者消费消息
- worker02：工作线程手动应答、重新入队、消息持久化演示      
- worker03：消息发布确认的三个方式，单个、批量、异步发布确认
- worker04：fanout扇型交换机演示
- worker05：direct直接交换机演示
- worker06：topic主题交换机演示
- worker07：死信队列演示
- worker08：优先级队列演示
- springboot-rabbitmq：集成springboot框架的MQ演示
    - 延迟队列
    - 自定义延迟队列
    - 延迟队列插件
    - 消息确认
    - 消息回退
    - 备份交换机

---

#### 笔记   

- **不公平分发：** 能者多劳，处理快的消费者，消费消息更多
- **预取值：** 信道没满的有机会继续消费消息，满了分配给信道没达到预取值的消费者
- **发布确认：**（满足三点，才能绝对保证消息不丢失）
    * 1.队列持久化；
    * 2.消息持久化；
    * 3.发布确认。
- **发布确认策略：**
    * 1.单个确认：同步发送，只有一个消息确认了，才会发下一条。缺陷是发送速度特别慢。
    * 2.多个确认：先发布一批消息，然后一起确认，可以极大的提高吞吐量，缺陷是发生故障，不知道是哪个消息出现问题。    
    * 3异步确认：性价比最高，无论可靠性和效率都不错，利用回调函数来达到消息的可靠性传递。map容器记录消息序号和内容，可以确认失败的消息。
- **交换机（Exchange)**
    * 生产者只能将消息发送到交换机
- **交换机类型**
````
1.直接（direct）：当消息发送到直接交换机上，会根据绑定队列的routingkey推送匹配的消息。
2.主题（topic）：将路由键和某模式进行匹配，符号"#"表示一个或者多个词，符号"*"表示一个词。routingkey必须是一个单词。
3.标题（headers）：通过键值对匹配的形式发送消息，不依赖routingKey。all表示多个键值对都要满足，any表示只需要满足其中一个。
4.扇出（fanout）：当消息发送到扇形交换机上，交换机会将消息路由给所有绑定这个交换机的队列上。
````

- **临时队列**
    * 一旦断开消费者连接，队列将被自动删除
    * 创建方式：channel.queueDeclare().getQueue();
- **binding**
    * exchange和queue之间的桥梁，能根据routingKey决定和哪个队列绑定
- **死信队列**
    * 无法被消费的消息
    * 应用于订单业务消息数据不丢失，当消息异常的时候，将消息投入死信队列
    * 来源：
        * 消息TTL过期
        * 队列达到最大长度
        * 消息被拒绝
- **延迟队列**
    * 延迟队列内部是有序的
    * 队列用来存放指定时间被处理的元素队列
    * 使用场景：
        * 订单10分钟内未支付，则自动取消
        * 用户注册，3天内没登录进行短信提醒
        * 用户发起退款，三天内没处理通知相关运营人员
        * 预定会议，开始时间前10分钟通知各个会议人员
    * 相较于使用定时任务检测，对于数据量比较大，并且时效性很强的场景，轮询是不可取的。所以延迟队列就有用了。
    * 通过安装插件处理延迟队列排队等待问题
    
- **延迟消息插件安装：**
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

- **发布确认**（避免rabbitMQ重启或者宕机导致消息投递失败、丢失）
    - 交换机、队列其中一个不存在就会丢失
    - 通过实现RabbitTemplate.ConfirmCallback进行交换机失败回调处理
    - 通过实现RabbitTemplate.ReturnsCallback进行队列失败回调处理
    
- **备份交换机**
    - 可以让交换机不回退消息给生产者
    - 无法投递消息的时候发送给备份交换机
    - 可以用于监控和报警 
    
- **消息重复消费（幂等性）问题处理**
    - 自己生成全局ID
    - 用唯一标识，比如时间戳、UUID、MQ自身消息ID判断
    - 消费端幂等性保障
        1. 唯一ID+指纹码机制，利用数据库主键去重，高并发场景下，单个数据库可能会存在写入性能瓶颈
        2. 利用redis原子性实现，setnx分布式锁
        
- **优先级队列 （0~255）**
    - 队列需要设置优先级，消息也需要设置优先级
    - 等消息全部发送到队列中，再进行消费，否则发送一个消费一个没意义。
    - 没设置的优先级最低，数字越大的优先级越高
    
- **惰性队列**
    - 设计目的是为了支持更长的队列，支持存储更多的消息
    - 使用场景，比如：消费者下线、宕机、关闭造成消息不能消费而堆积。
    - 消息保存在磁盘中，比存在内存中处理要慢，性能较低
    - 两种模式：default和lazy，惰性队列就是lazy模式
    - 一百万条消息，1条占用1kb，普通队列占用内存1.2G，而惰性队列只占用1.5MB，因为消息存储在磁盘中。
    - 通过arguments.put("x-queue-mode","lazy")使用;
    
- **RabbitMQ集群**
    - 修改机器名称：vim /etc/hostname，比如有3台，各自命名 node1,node2,node3
    - 配置每台机器的host：vim /etc/hosts，3台机器都要配置上node1,node2,node3
    - 在node1上通过远程命令确保每台机器的cookie是同一个值
    ```xml
    scp /var/lib/rabbitmq/.erlang.cookie root@node2:/var/lib/rabbitmq/.erlang.cookie
    node3更换名称同理
    之后每台机器都要重启
    rabbitmq-server -detached
    ``` 
    - 节点2执行
    ```xml
    rabbitmq stop_app 关闭RabbitMQ服务
    (rabbitmqctl stop 停止Erlang虚拟机，这个不用执行)
    rabbitmqctl join_cluster rabbit@node1
    rabbitmqctl start_app
    ``` 
    - 节点3同上，将第三个指令改成加入到node2节点
    - 集群状态查询（Disk Nodes展示当前集群，Running表示运行的节点，Cluster name显示当前节点名称）
    ```xml
    rabbitmqctl cluster_status
    ```
    - 创建账号
   ```xml
    rabbitmqctl add_user admin 123
    ```
    - 设置用户角色
    ```xml
    rabbitmqctl set_user_tags admin administrator
    ```
    - 设置用户权限
    ```xml
    rabbitmqctl set_permission -p "/" admin ".*" ".*" ".*"
    ```
    - 登录RabbitMQ后台，首页Nodes展示有当前集群节点
    - 解除集群节点(每个节点各自执行)
    ```xml
    rabbitmqctl stop_app
    rabbitmqctl reset
    rabbitmqctl start_app
    rabbitmqctl cluster_status
    rabbltmqctl forget_cluster_node rabbit@node2 （这段指令在node1主节点上执行）
    ```

- **镜像队列**
    - 为了避免集群中只有一个broker节点失效或者宕机导致整体服务临时不可用
    - 镜像队列将集群中队列镜像到其他broker节点上，如果一个节点失效，可以自动切换到集群上的其他节点上保证服务器可用性
    - 搭建步骤
        - 启动集群节点
        - 打开其中一个节点的后台, Admin -> Policies -> Add / update a policy
        ```xml
         name (名字随便起，没有意义，只是表示名字)
         Pattern 规则，^mirror 表示备份以mirror开头的队列
         Apply to, Exchange and queues 表示交换机和队列都可以
         Definition 应用的参数: 
              ha-demo = exactly(备机模式，指令模式)
              ha-param = 2 (备机参数2份)
              ha-sync-mode = automatic (同步模式，自动)
        ```
        - 之后在任意节点创建一个mirror开头的队列，就随机在其他一个节点镜像一个
        - 假设在node1上创建了消息队列，那么随机到node2节点上会镜像一个。
        如果node1宕机了，那么node2又会在node3上镜像一个，直到没有机器可用，否则会一直保存两份。

- **Haproxy实现负载均衡**
    - 扩展Nginx\lvs\Haproxy区别，都可以实现负载均衡
    - 因为生产者连接服务器集群是固定IP的，所以IP地址服务器挂了没办法变更IP
    - Haproxy + Keepalive实现高可用高并发负载均衡
    
- **Federation Exchange(联合交换机)**
    - broker北京和broker深圳两个机房相距很远，有网络延迟问题
    - 北京用户访问北京broker，深圳用户访问深圳broker，通过数据同步来保证机房之间数据一致
    - 搭建步骤
        - 保证每台机器节点可用
        - 每台机器开启federation相关插件
        ```xml
        rabbitmq-plugins enable rabbitmq_federation
        rabbitmq-plugins enable rabbitmq_feaderation_management
        打开后台Admin界面可以看到新安装的插件
        ```
        - 上游数据要往下游走
        - 同步数据以交换机为节点
        - node1的数据同步给node2之前，一定要node2有fed_exchange交换机
        - 创建完交换机之后，下游node2配置上游node1的地址
        ```xml
        打开node2后台 admin -> Federation Upstreams -> add a new upstream
        Name (名字，可以随便起)
        URI (amqp://账号:密码@节点名) amqp://guest:guest@node1
        ```
        - 添加策略
        ```xml
        Name （名字随便起）
        Pattern，规则（^fed.*）
        Apply to : exchanges
        Definition 策略，federation-upsteam = 上一步起的上游节点名Name
        ```
        - 处理完成后，在Federation status可以看到状态，看是否添加成功
     
     - **Federation Queue(联合队列)**  
        - 消费者达到消息的负载均衡
        - 搭建步骤
            - 配置 federation-upsteam，上一步已经做了，这里可以忽略
            - 添加策略
        ```xml
        Name （名字随便起）
                Pattern，规则（^fed.*）
                Apply to : queues
                Definition 策略，federation-upsteam = 上一步起的上游节点名Name
        ```
- **Shovel**
    - 和Federation数据转发功能类似
    - Shvel能够负责连接源和目的地、负责消息的读写以及连接失败问题的处理
    - 源头发送的消息直接会进入到目的地队列，Q1队列的数据会被Shovel同步到Q2
    - 搭建步骤
        - 开启插件(所有机器都要开启)
        ```xml
        rabbitmq-plugins enable rabbitmq_shovel
        rabbitmq-plugins enable rabbitmq_shovel_management
        ```
        - 安装完毕后，打开后台 Admin -> Shovel Management
        - 配置源端和目的地
        ```xml
        上面是源端
        配置Name （随便起）
        URI 源端地址 (amqp://账号:密码@节点名) amqp://guest:guest@node1
        URI后面是队列名称
        下面Destination是目的地
        配置node2 同上
        比如，源端是北京，目的地是深圳
        ``` 