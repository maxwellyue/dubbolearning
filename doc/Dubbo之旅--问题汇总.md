内容来自：

[Dubbo之旅--问题汇总](http://blog.csdn.net/jnqqls/article/details/45399357)

[dubbo 问题整理](http://blog.csdn.net/luwei42768/article/details/54847427)



---
###### 增加提供服务版本号和消费服务版本号

这个具体来说不算是一个问题,而是一种问题的解决方案,

在我们的实际工作中会面临各种环境资源短缺的问题,也是很实际的问题,

刚开始我们还可以提供一个服务进行相关的开发和测试,

但是当有多个环境多个版本,多个任务的时候就不满足我们的需求,

这时候我们可以通过给提供方增加版本的方式来区分.

这样能够剩下很多的物理资源,同时为今后更换接口定义发布在线时，

可不停机发布，使用版本号.

引用只会找相应版本的服务



---
###### 服务超时问题

此问题也是在项目中非常常见的一个问题,但是这个问题背后可能是各种原因导致.

目前如果存在超时，情况基本都在如下：


(1)服务请求超时.


(2)调用的版本不对.

在上面我们已经说了具体的版本问题,如果你调用的对方版本不对的话,就相当于你的消费者没有提供者.所以会出现超时,此时只需要把版本对应好即可.

(3)提供者的服务被禁止

这是一种人为的控制,通过监控中心我们可以对具体的服务,以及它的权重进行控制,当我将一个具体的服务禁止之后消费者就调不到相关的服务,此时就会出现超时的问题.解决方案,取消禁止即可.注意这里有一定时间的缓存,实际操作的时候应该注意.


---
###### 服务保护

服务保护的原则上是避免发生类似雪崩效应，尽量将异常控制在服务周围，不要扩散开。

说到雪崩效应，还得提下dubbo自身的重试机制，默认3次，当失败时会进行重试，

这样在某个时间点出现性能问题，然后调用方再连续重复调用，很容易引起雪崩，

建议的话还是很据业务情况规划好如何进行异常处理，何时进行重试。

服务保护的话

考虑服务的dubbo线程池类型（fix线程池的话考虑线程池大小）、数据库连接池、dubbo连接数限制是否都合适.


---
###### 注册中心的分组group和服务的不同实现group

这两个东西完全不同的概念，使用的时候不要弄混了。

registry上可以配置group，用于区分不同分组的注册中心，

比如在同一个注册中心下，有一部分注册信息是要给开发环境用的，有一部分注册信息时要给测试环境用的，可以分别用不同的group区分开，

目前对这个理解还不透彻，大致就是用于区分不同环境。

service和reference上也可以配置group，

这个用于区分同一个接口的不同实现，只有在reference上指定与service相同的group才会被发现。


---
###### Dubbo中zookeeper做注册中心，如果注册中心集群都挂掉，发布者和订阅者之间还能通信么？
可以，启动dubbo时，消费者会从zk拉取注册的生产者的地址接口等数据，缓存在本地。

每次调用时，按照本地存储的地址进行调用

注册中心对等集群，任意一台宕掉后，会自动切换到另一台

注册中心全部宕掉，服务提供者和消费者仍可以通过本地缓存通讯

服务提供者无状态，任一台 宕机后，不影响使用

服务提供者全部宕机，服务消费者会无法使用，并无限次重连等待服务者恢复

---
###### dubbo连接注册中心和直连的区别
在开发及测试环境下，经常需要绕过注册中心，只测试指定服务提供者，这时候可能需要点对点直连，

点对点直联方式，将以服务接口为单位，忽略注册中心的提供者列表，

服务注册中心，动态的注册和发现服务，使服务的位置透明，并通过在消费方获取服务提供方地址列表，实现软负载均衡和Failover，

注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。

服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。

注册中心负责服务地址的注册与查找，相当于目录服务，服务提供者和消费者只在启动时与注册中心交互，注册中心不转发请求，

注册中心，服务提供者，服务消费者三者之间均为长连接，监控中心除外，

注册中心通过长连接感知服务提供者的存在，服务提供者宕机，注册中心将立即推送事件通知消费者

注册中心和监控中心全部宕机，不影响已运行的提供者和消费者，消费者在本地缓存了提供者列表

注册中心和监控中心都是可选的，服务消费者可以直连服务提供者

---
###### Dubbo在安全机制方面是如何解决的
Dubbo通过Token令牌防止用户绕过注册中心直连，然后在注册中心上管理授权。

Dubbo还提供服务黑白名单，来控制服务所允许的调用方。