package com.yuan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建一个服务提供者 (eureka client)
 *
 *
 * 当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。
 * Eureka server 从每个client实例接收心跳消息。
 * 如果心跳超时，则通常将该实例从注册server中删除。
 *
 * 仅仅@EnableEurekaClient是不够的，还需要在配置文件中注明自己的服务注册中心的地址
 */
@EnableEurekaClient //表明是一个eurekaclient
@SpringBootApplication
@RestController
public class ServiceHiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceHiApplication.class, args);
	}

	@Value("${server.port}")
	String port;

	@RequestMapping("/hi")
	public String home(@RequestParam String name) {
		return "hi "+name+",i am from port:" +port;
	}
}
