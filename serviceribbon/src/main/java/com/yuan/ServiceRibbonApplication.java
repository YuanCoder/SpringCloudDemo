package com.yuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 向服务注册中心注册一个新的服务，这时service-ribbon既是服务提供者，也是服务消费者
 *
 * 通过@EnableDiscoveryClient向服务中心注册；并且注册了一个bean: restTemplate;通过@ LoadBalanced注册表明，这个restRemplate是负载均衡的。
 *
 */
@SpringBootApplication
@EnableDiscoveryClient //向服务中心注册
@EnableHystrix
@EnableHystrixDashboard //开启hystrixDashboard
public class ServiceRibbonApplication {

	public static void main(String[] args){
		SpringApplication.run(ServiceRibbonApplication.class, args);
	}

	@Bean
	@LoadBalanced //@ LoadBalanced注册表明，这个restRemplate是负载均衡的。
	RestTemplate restTemplate(){
		return  new RestTemplate();
	}
}
