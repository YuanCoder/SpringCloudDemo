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
   <strong>踩坑 最新版本的feign是自带断路器的已经默认关闭，需要手动打开.</strong>

   Circuit Breaker: Hystrix Dashboard (断路器：hystrix 仪表盘) </br>
   1 主程序入口中加入@EnableHystrixDashboard注解，开启hystrixDashboard </br>
   2 打开浏览器：访问http://localhost:8764/hystrix  </br>
   3 Hystrix Dashboard 下输入 http://localhost:8764/hystrix.stream    title 随便输入一个标识 </br>
   4 点击monitor stream，会出现监控界面. </br>
   5 访问：http://localhost:8764/hi?name=yuan ,监控界面会动态显示 请求的次数以及其它信息. </br>


# 路由网关(zuul)
     在微服务架构中，需要几个关键的组件，服务注册与发现、服务消费、负载均衡、断路器、智能路由、配置管理等，<br>
     由这几个组件可以组建一个简单的微服务架构。<br>

  以gitHub 为例 <br>
     客户端的请求首先经过负载均衡（zuul、Ngnix），再到达服务网关（zuul集群），然后再到具体的服务，服务统一 <br>
     注册到高可用的服务注册中心集群，服务的所有的配置文件由配置服务管理（下一篇文章讲述），配置服务的配置文 <br>
     件放在Git仓库，方便开发人员随时改配置。 <br>

  Zuul简介

    Zuul的主要功能是路由和过滤器。路由功能是微服务的一部分，比如/api/user映射到user服务，/api/shop映射到shop <br>
    服务。zuul实现了负载均衡。（理解意思：zuul主要作用是请求转发，和过滤，请求转发是做了负载均衡。）<br>

    首先向eureka注册自己，端口为8769，服务名为service-zuul；以/api-a/ 开头的请求都指向service-ribbon；<br>
    以/api-b/开头的请求都指向service-feign；<br>

    依次运行这五个工程;打开浏览器访问：http://localhost:8769/api-a/hi?name=yuan ;浏览器显示： <br>

    hi yuan,i am from port:8762   <br>
    打开浏览器访问：http://localhost:8769/api-b/hi?name=yuan ;浏览器显示：  <br>

    hi yuan,i am from port:8762  <br>
    这说明zuul起到了路由的作用； <br>

    zuul不仅只是路由，并且还能过滤，做一些安全验证，譬如：服务过滤。<br>

    <strong>服务过滤</strong> <br>

    filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下： <br>
        pre：路由之前 <br>
        routing：路由之时 <br>
        post： 路由之后 <br>
        error：发送错误调用 <br>
    filterOrder：过滤的顺序 <br>
    shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。 <br>
    run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。 <br>

    这时访问：http://localhost:8769/api-a/hi?name=yuan ；网页显示：<br>

    token is empty
    访问 http://localhost:8769/api-a/hi?name=yuan&token=520 ；
    网页显示： <br>

    hi yuan,i am from port:8763



# 分布式配置中心(Spring Cloud Config)

    在分布式系统中，spring cloud config 提供一个服务端和客户端去提供可扩展的配置服务。我们可用用配置服务  <br>
    中心区集中的管理所有的服务的各种环境配置文件。配置服务中心采用Git的方式存储配置文件，因此我们很容易部  <br>
    署修改，有助于对环境配置进行版本管理。 <br>

    主要讲：一个服务如何从配置中心读取文件，配置中心如何从远程Git读取配置文件 <br>

    config-server <br>
    配置中心配置如下:   <br>
        spring.cloud.config.server.git.uri：配置git仓库地址   <br>
        spring.cloud.config.server.git.searchPaths：配置仓库路径   <br>
        spring.cloud.config.label：配置仓库的分支   <br>
        spring.cloud.config.server.git.username：访问git仓库的用户名   <br>
        spring.cloud.config.server.git.password：访问git仓库的用户密码   <br>

    远程仓库https://github.com/forezp/SpringcloudConfig/ 中又个文件config-client-dev.properties   <br>
    文件中有一个属性：foo = foo version 4   <br>


    启动程序：访问http://localhost:8888/foo/dev   <br>

        {"name":"foo","profiles":["dev"],"label":"master",  <br>
        "version":"792ffc77c03f4b138d28e89b576900ac5e01a44b","state":null,"propertySources":[]}   <br>

    证明配置服务中心可以从远程程序获取配置信息。  <br>

    http请求地址和资源文件映射如下:   <br>

        /{application}/{profile}[/{label}]   <br>
        /{application}-{profile}.yml   <br>
        /{label}/{application}-{profile}.yml   <br>
        /{application}-{profile}.properties   <br>
        /{label}/{application}-{profile}.properties   <br>


    config-client <br>

        其配置文件： <br>

        spring.application.name=config-client  <br>
        spring.cloud.config.label=master  <br>
        spring.cloud.config.profile=dev  <br>
        spring.cloud.config.uri= http://localhost:8888/  <br>
        server.port=8881  <br>

        spring.cloud.config.label 指明远程仓库的分支  <br>
        spring.cloud.config.profile  <br>

        dev开发环境配置文件  <br>
        test测试环境  <br>
        pro正式环境  <br>
        spring.cloud.config.uri= http://localhost:8888/ 指明配置服务中心的网址。 <br>

        打开网址访问：http://localhost:8881/hi，网页显示：   <br>

        foo version 4   <br>
        这就说明，config-client从config-server获取了foo的属性，而config-server是从git仓库读取的.   <br>


# 高可用的分布式配置中心(Spring Cloud Config)

    当配置中心服务很多时，都需要同时从配置中心读取文件的时候，这时我们可以考虑将配置中心做成一个微服务，   <br>
    并且将其集群化，从而达到高可用。    <br>

    创建一个eureka-server2工程，用作服务中心。    <br>
    改造config-server (将其注册为服务)<br>
    改造config-client (将其注册微 eureka服务)</br>

        spring.cloud.config.discovery.enabled 是非从配置中心读取文件
        spring.cloud.config.discovery.serviceId 配置中心的servieId，即服务名

    这时发现，在读取配置文件不再写ip地址，而是服务名，这时如果配置服务部署多份，通过负载均衡，从而高可用。

    一次启动eureka-servr,config-server,config-client
    访问网址：http://localhost:8889/

    访问http://localhost:8881/hi，浏览器显示：

    foo version 4


# 消息总线(Spring Cloud Bus)

    spring Cloud Bus 将分布式的节点和轻量的消息代理连接起来。这可以用于广播配置文件的更改或者其他的管理工作。
    一个关键的思想就是，消息总线可以为微服务做监控，也可以作为应用程序之间相互通讯。本文要讲述的是用AMQP实现
    通知微服务架构的配置文件的更改。

    按照官方文档，我们只需要在配置文件中配置 spring-cloud-starter-bus-amqp ；这就是说我们需要装rabbitMq。

    说一下基本原理：
        git 上存放我们的远程配置文件
        config-server 连接到 git
        config-client 连接到config-server
        当我们启动config-client 服务的时候，client 会通过连接的 config-server 拿到远程git 上面的配置文件，然
        后通过 Spring 加载到对象中。


    依次启动eureka-server、confg-cserver,启动两个config-client，端口为：8881、8882。

            访问http://localhost:8881/hi 或者http://localhost:8882/hi 浏览器显示：

            foo version 4

    去代码仓库将foo的值改为“foo version 0304”，即改变配置文件foo的值。如果是传统的做法，可以需要重启服务，才能
    达到配置文件的更新。此时，我们只需要用post请求：http://localhost:8881/bus/refresh
    重新读取配置文件

    这时我们再访问http://localhost:8881/hi 或者http://localhost:8882/hi 浏览器显示：

    foo version 0304

    另外，/bus/refresh接口可以指定服务，即使用”destination”参数，比如 “/bus/refresh?destination=customers:**”
    刷新服务名为customers的所有服务，不管ip。

    当Git文件更改的时候，通过pc端用post 向端口为8882的config-client发送请求/bus/refresh/；此时8882端口会发送一个
    消息，由消息总线向其他服务传递，从而使整个微服务集群都达到更新配置文件。
    <strong>踩坑</strong>
        1 /bus/refresh现在只接受POST请求
        2 要在 config-client 里加入 @RefreshScope 这个注解，然后在peroperites里面要加入
          management.security.enabled=false 来保证调用 /bus/refresh的时候不需要验证。

# 服务链路追踪(Spring Cloud Sleuth)

    服务追踪组件zipkin，spring Cloud Sleuth集成了zipkin组件。
    Spring Cloud Sleuth 主要功能就是在分布式系统中提供追踪解决方案，并且兼容支持了 zipkin，你只需要在pom文件中
    引入相应的依赖即可。

    服务追踪分析  <br>
    微服务架构上通过业务来划分服务的，通过REST调用，对外暴露的一个接口，可能需要很多个服务协同才能完成这个接口功能，
    如果链路上任何一个服务出现问题或者网络超时，都会形成导致接口调用失败。随着业务的不断扩张，服务之间互相调用会越
    来越复杂。   <br>
    随着服务的越来越多，对调用链的分析会越来越复杂。

    术语 <br>
       - Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯
                一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span
                的ID、以及进度ID(通常是IP地址)   <br>
       - span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。
       - Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。
       - Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束
           1 cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
           2 sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
           3 ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的
                处理请求时间   <br>
           4 cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端
                从服务端获取回复的所有所需时间

        基本知识讲解完毕，下面我们来实战，本文的案例主要有三个工程组成:一个server-zipkin,它的主要作用使用ZipkinServer
        的功能，收集调用数据，并展示；一个service-hi,对外暴露hi接口；一个service-miya,对外暴露miya接口；这两个service
        可以相互调用；并且只有调用了，server-zipkin才会收集数据的，这就是为什么叫服务追踪了。

        依次启动上面的server-zipkin service-hi  service-miya 三个工程，打开浏览器访问：http://localhost:9411/

        访问：http://localhost:8989/miya，浏览器出现：

        i’m service-hi2
        再打开http://localhost:9411/的界面，点击Dependencies,可以发现服务的依赖关系。

        点击find traces,可以看到具体服务相互调用的数据。


# 高可用的服务注册中心

    Eureka通过运行多个实例，使其更具有高可用性。事实上，这是它默认的熟性，你需要做的就是给对等的实例一个合法的关联serviceurl。

    改造工作
    在eureka-server工程中resources文件夹下，创建配置文件application-peer1.yml:
            server:
              port: 8761

            spring:
              profiles: peer1
            eureka:
              instance:
                hostname: peer1
              client:
                serviceUrl:
                  defaultZone: http://peer2:8769/eureka/

    并且创建另外一个配置文件application-peer2.yml：

            server:
              port: 8769

            spring:
              profiles: peer2
            eureka:
              instance:
                hostname: peer2
              client:
                serviceUrl:
                  defaultZone: http://peer1:8761/eureka/

    这时eureka-server就已经改造完毕。
    按照官方文档的指示，需要改变etc/hosts
        127.0.0.1 peer1
        127.0.0.1 peer2

    这时需要改造下service-hi:
        eureka:
          client:
            serviceUrl:
              defaultZone: http://peer1:8761/eureka/
        server:
          port: 8762
        spring:
          application:
            name: service-hi

    启动工程
        启动eureka-server：

        Java -jar eureka-server-0.0.1-SNAPSHOT.jar - -spring.profiles.active=peer1

        java -jar eureka-server-0.0.1-SNAPSHOT.jar - -spring.profiles.active=peer2
        >

        启动service-hi:

        java -jar service-hi-0.0.1-SNAPSHOT.jar

        你会发现注册了service-hi，并且有个peer2节点，同理访问localhost:8769你会发现有个peer1节点。

        client只向8761注册，但是你打开8769，你也会发现，8769也有 client的注册信息。

        个人感受：这是通过看官方文档的写的demo ，但是需要手动改host是不是不符合Spring Cloud 的高上大？

        eureka.instance.preferIpAddress=true是通过设置ip让eureka让其他服务注册它。也许能通过去改变去通过改变host的方式。


        Eureka-eserver peer1 8761,Eureka-eserver peer2 8769相互感应，当有服务注册时，两个Eureka-eserver是对等的，
        它们都存有相同的信息，这就是通过服务器的冗余来增加可靠性，当有一台服务器宕机了，服务并不会终止，因为另一台服务
        存有相同的数据。