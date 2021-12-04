package com.endwas.service;

import org.springframework.annotation.Component;
import org.springframework.annotation.PostConstruct;

/**
 * 订单服务
 *
 * @author endwas
 * @date Created in 2021/12/4 14:30
 */
@Component("orderService")
public class OrderService {
    public void say() {
        System.out.println("orderService say");
    }

    @PostConstruct
    public void post(){
        System.out.println("postConstruct method");
    }

}
