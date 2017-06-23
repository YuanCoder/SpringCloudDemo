package com.yuan.service.impl;/**
 * Created by Yuanjp on 2017/6/23 0023.
 */

import com.yuan.service.SchedualServiceHi;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Yuanjp
 * @create 2017-06-23-16:28
 */
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {

    @Override
    public String sayHiFromClientOne(@RequestParam(value = "name") String name) {
        return "sorry "+name;
    }
}
