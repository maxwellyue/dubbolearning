### 一、环境准备
###### 安装zookeeper
为啥要安装：使用zookeeper作为dubbo的注册中心。

①安装zookeeper
```
brew install zookeeper 
```
安装完成后，配置文件在/usr/local/etc/zookeeper/zoo.cfg 

②启动zookeeper
```
brew services start zookeeper
# 启动成功后，会在控制台看到如下提示
# ==> Successfully started `zookeeper` (label: homebrew.mxcl.zookeeper)
```

###### 运行dubbo控制台
为啥要运行：可以在之后启动项目后，查看生产者和消费者应用运行情况。

①下载dubbo源码并编译
```
git clone https://github.com/alibaba/dubbo.git
cd dubbo
git checkout master(或者git checkout -b dubbo-2.4.0
mvn clean install -Dmaven.test.skip
```
`install`命令会执行一段时间，耐心等待。

②运行dubbo-admin
```
cd dubbo/dubbo-admin
mvn jetty:run -Ddubbo.registry.address=zookeeper://127.0.0.1:2181
```
如果之前没有用过jetty，maven会自动下载相关依赖，需要等待一段时间。

也可以直接将`dubbo/dubbo-admin/target/dubbo-admin-2.5.4-SNAPSHOT.war`的这个war包放在tomcat的webapps目录下用tomcat启动（我这种方式没有启动成功。。。）。


---
### 二、demo项目搭建
新建`dubbolearning`工程，只需要一个pom文件，作为其他项目的容器。

然后在`dubbolearning`工程中添加三个子工程：
* dubbo-api：服务的接口
* dubbo-provider：接口的实现（提供者），需要依赖`dubbo-api`
* dubbo-consumer：消费者，需要依赖`dubbo-api`

###### 1、`dubbo-api`

仅仅是接口的编写，不依赖任何jar，也不需要向dubbo注册。

pom文件
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>dubbolearning</artifactId>
        <groupId>com.maxwell</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>dubbo_api</artifactId>
</project>
```


###### 2、`dubbo-provider`
服务的具体实现，需要向dubbo的注册中心zookeeper注册自己可以提供哪些服务（因此需要依赖zookeeper和dubbo），同时需要依赖`dubbo-api`项目。

dubbo_provider.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
    http://code.alibabatech.com/schema/dubbo
    http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="dubbo_provider" />

    <!-- 使用zookeeper注册中心暴露服务地址 -->
    <dubbo:registry address="zookeeper://127.0.0.1:2181" />

    <!-- 用dubbo协议在20880端口暴露服务 -->
    <dubbo:protocol name="dubbo" />

    <!-- 声明需要暴露的服务接口 -->
    <dubbo:service interface="com.maxwell.dubbolearning.service.TestService" ref="testService" />
    <!-- 具体的实现bean -->
    <bean id="testService" class="com.maxwell.dubbolearning.service.impl.TestServiceImpl" />

</beans>
```

###### 3、`dubbo-consumer`
服务的消费者，需要向dubbo的注册中心zookeeper注册自己需要哪些服务（因此需要依赖zookeeper和dubbo），同时需要依赖`dubbo-api`项目。

dubbo_consumer.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
    http://code.alibabatech.com/schema/dubbo
    http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <!--消费者：向注册中心订阅服务-->


    <!-- 提供方应用信息，用于计算依赖关系 -->
    <dubbo:application name="dubbo_consumer" />

    <!-- 使用zookeeper注册中心暴露服务地址 -->
    <dubbo:registry address="zookeeper://127.0.0.1:2181" />

    <!-- 声明需要使用的服务接口,即作为消费者，只需要知道接口即可，不依赖具体实现 -->
    <dubbo:reference interface="com.maxwell.dubbolearning.service.TestService" id="testService" check="false" />

</beans>
```

### 启动项目
启动`dubbo-provider`和`dubbo-consumer`，刷新dubbo控制台页面，可以看到有1个提供者，1个消费者，共2个应用。

![dubbo 控制台 2017-07-13 下午10.29.50.png](http://upload-images.jianshu.io/upload_images/1932449-dd0d42f4a39a36c5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


---
#### 参考
[搭建dubbo+zookeeper+spring环境-编码篇](http://mushanshitiancai.github.io/2016/07/29/java/%E6%90%AD%E5%BB%BAdubbo-zookeeper-spring%E7%8E%AF%E5%A2%83-%E7%BC%96%E7%A0%81%E7%AF%87/)

[官方dubbo-demo](https://github.com/alibaba/dubbo/tree/master/dubbo-demo)