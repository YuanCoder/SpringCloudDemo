# SpringCloudDemo
SpringCloud 学习

# spring cloud简介
  spring cloud 为开发人员提供了快速构建分布式系统的一些工具，包括配置管理、服务发现、断路器、路由、微代理、事件总线、
  全局锁、决策竞选、分布式会话等等。它运行环境简单，可以在开发人员的电脑上跑。

# 服务的注册与发现（Eureka）
  用组件上Spring Cloud Netflix的Eureka ,eureka是一个服务注册和发现模块。
  model工程Eureka Server作为服务注册中心
  model工程Eureka Client作为服务提供者
  当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。
  Eureka server 从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。

   http://localhost:8762/hi?name=yuan

# 服务消费者（rest+ribbon）

  服务与服务的通讯是基于http restful的。
  spring cloud有两种调用方式，一种是ribbon+restTemplate，另一种是feign。在这一篇文章首先讲解下基于ribbon+rest。

  ribbon简介

  ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign也用到ribbon，当你使用@ FeignClient，ribbon自动被应用。
  ribbon 已经默认实现了这些配置bean：

  IClientConfig ribbonClientConfig: DefaultClientConfigImpl
  IRule ribbonRule: ZoneAvoidanceRule
  IPing ribbonPing: NoOpPing
  ServerList ribbonServerList: ConfigurationBasedServerList
  ServerListFilter ribbonServerListFilter: ZonePreferenceServerListFilter
  ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer

- 一个服务注册中心，eureka server,端口为8761.
- service-hi工程跑了两个副本，端口分别为8762,8763，分别向服务注册中心注册.
- sercvice-ribbon端口为8764,向服务注册中心注册.
- 当sercvice-ribbon通过restTemplate调用service-hi的hi接口时，因为用ribbon进行了负载均衡，会轮流的调用service-hi：8762和8763 两个端口的hi接口.

  http://localhost:8764/hi?name=yuan