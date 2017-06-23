# SpringCloudDemo
SpringCloud 学习

# spring cloud简介
  spring cloud 为开发人员提供了快速构建分布式系统的一些工具，包括配置管理、服务发现、断路器、路由、微代理、事件总线、 </br>
  全局锁、决策竞选、分布式会话等等。它运行环境简单，可以在开发人员的电脑上跑。</br>

# 服务的注册与发现（Eureka）
  用组件上Spring Cloud Netflix的Eureka ,eureka是一个服务注册和发现模块。</br>
  Eureka Server(model工程 eurekaserver)作为服务注册中心。 </br>
  Eureka Client(model工程 servicehi)作为服务提供者。 </br>
  当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。</br>
  Eureka server 从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。</br>
   http://localhost:8761 </br>
   http://localhost:8762/hi?name=yuan

# 服务消费者（rest+ribbon）

  服务与服务的通讯是基于http restful的。</br>
  spring cloud有两种调用方式，一种是ribbon+restTemplate，另一种是feign。在这一篇文章首先讲解下基于ribbon+rest。</br>

  ribbon简介

    ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign也用到ribbon，当你使用@ FeignClient，</br>
    ribbon自动被应用。ribbon 已经默认实现了这些配置bean：

        IClientConfig ribbonClientConfig: DefaultClientConfigImpl </br>
        IRule ribbonRule: ZoneAvoidanceRule </br>
        IPing ribbonPing: NoOpPing </br>
        ServerList ribbonServerList: ConfigurationBasedServerList </br>
        ServerListFilter ribbonServerListFilter: ZonePreferenceServerListFilter </br>
        ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer </br>

- 一个服务注册中心，eureka server,端口为8761.
- service-hi工程跑了两个副本，端口分别为8762,8763，分别向服务注册中心注册.
- sercvice-ribbon端口为8764,向服务注册中心注册.
- 当sercvice-ribbon通过restTemplate调用service-hi的hi接口时，因为用ribbon进行了负载均衡，会轮流的调用service-hi：8762和8763 两个端口的hi接口.

  http://localhost:8764/hi?name=yuan


# 服务消费者（Feign）

  Feign简介 </br>

  Feign是一个声明式的web服务客户端，它使得写web服务变得更简单。使用Feign,只需要创建一个接口并注解。它具有可插</br>
  拔的注解特性，包括Feign 注解和JAX-RS注解。Feign同时支持可插拔的编码器和解码器。spring cloud对Spring mvc添加 </br>
  了支持，同时在spring web中次用相同的HttpMessageConverter。当我们使用feign的时候，spring cloud 整和了Ribbon </br>
  和eureka去提供负载均衡。</br>

  简而言之： </br>

  - feign采用的是接口加注解
  - feign 整合了ribbon

    在程序的入口类中添加 @EnableFeignClients来开启feign. </br>
    通过定义一个feign接口类,添加 @ FeignClient（“服务名”），来指定调用哪个服务. </br>

    启动eureka-server，端口为8761; 启动service-hi两次 端口分别为8762 、8773

    http://localhost:8765/hi?name=yuan

    更改feign的配置：在声明feignclient的时候，不仅要指定服务名，同时需要制定服务配置类。 </br>
    配置类需要加@Configuration注解，重写两个Bean,具体看官方文档：</br>
    http://projects.spring.io/spring-cloud/spring-cloud.html#spring-cloud-feign </br>


# 断路器（Hystrix）
    为了解决 单个（或多个）服务引起任务累计，导致服务瘫痪或者服务“雪崩”问题，就出现断路器模型</br>

  断路器简介</br>
    Netflix已经创建了一个名为Hystrix的库来实现断路器模式。 在微服务架构中，
    多层服务调用是非常常见的。</br>
    较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用达到一个
    阀值（hystric 是5秒20次） 断路器将会被打开。</br>
    断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。</br>

  在ribbon使用断路器</br>
    - 在程序的入口类加@EnableHystrix  </br>
    - service(抽象)  HelloService类加@HystrixCommand，并指定fallbackMethod方法   </br>


    启动eureka-server 工程；启动service-hi工程，它的端口为8763</br>
    启动：service-ribbon 工程，当我们访问http://localhost:8764/hi?name=yuan</br>
    出现 hi yuan,i am from port:8763</br>

    关闭service-hi服务再次访问 http://localhost:8764/hi?name=yuan</br>
    出现 hi,yuan,sorry,error!</br>
    这就证明断路器起作用了。</br>

  在Feign中使用断路器</br>
     使用了feign，feign是自带断路器的，并且是已经打开了。如果使用feign不想用断路器的话，可以在配置文件中关闭它，</br>
     properties中配置：    feign.hystrix.enabled=false </br>
     yml中配置：           hystrix: enabled: false </br>
     注意有的版本中已经默认关闭，so 注意版本。</br>

     - 只需要在SchedualServiceHi接口的注解中加上fallback的指定类就行了

      启动eureka-server 工程；启动service-hi工程，它的端口为8763</br>
         启动：service-ribbon 工程，当我们访问http://localhost:8765/hi?name=yuan</br>
         出现 hi yuan,i am from port:8763</br>
      关闭service-hi服务再次访问 http://localhost:8765/hi?name=yuan</br>
         出现 sorry yuan</br>
         这就证明断路器起作用了。</br>
    # 踩坑 最新版本的feign是自带断路器的已经默认关闭，需要手动打开.
