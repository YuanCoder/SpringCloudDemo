package com.yuan.service;/**
 * Created by Yuanjp on 2017/6/23 0023.
 */

import com.yuan.service.impl.SchedualServiceHiHystric;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * feign接口类,通过@ FeignClient（“服务名”），来指定调用哪个服务
 *
 * @author Yuanjp
 * @create 2017-06-23-11:20
 */
@FeignClient(value = "service-hi",fallback = SchedualServiceHiHystric.class) //指定调用哪个服务
public interface SchedualServiceHi {

    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
