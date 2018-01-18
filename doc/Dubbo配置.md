
##### dubbo:reference 的一些属性的说明：

1）interface调用的服务接口

2）check 启动时检查提供者是否存在，true报错，false忽略

3）registry 从指定注册中心注册获取服务列表，在多个注册中心时使用，值为`<dubbo:registry>`的id属性，多个注册中心ID用逗号分隔

4）loadbalance 负载均衡策略，可选值：`random,roundrobin,leastactive`，分别表示：随机，轮循，最少活跃调用


---
##### dubbo:registry 标签一些属性的说明：

1）register是否向此注册中心注册服务，如果设为false，将只订阅，不注册。

2）check注册中心不存在时，是否报错。

3）subscribe是否向此注册中心订阅服务，如果设为false，将只注册，不订阅。

4）timeout注册中心请求超时时间(毫秒)。

5）address可以Zookeeper集群配置，地址可以多个以逗号隔开等。

---
##### dubbo:service标签的一些属性说明：

1）interface服务接口的路径

2）ref引用对应的实现类的Bean的ID

3）registry向指定注册中心注册，在多个注册中心时使用，值为<dubbo:registry>的id属性，多个注册中心ID用逗号分隔，如果不想将该服务注册到任何registry，可将值设为N/A

4）register 默认true ，该协议的服务是否注册到注册中心。