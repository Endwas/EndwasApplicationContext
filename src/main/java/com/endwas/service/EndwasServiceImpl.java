package com.endwas.service;

import org.springframework.annotation.Autowired;
import org.springframework.annotation.Component;

/**
 * @author endwas
 * @date Created in 2021/12/4 14:29
 */
@Component("endwasService")
public class EndwasServiceImpl implements EndwasService{

    @Autowired
    private OrderService orderService;

    @Override
    public void execute() {
        orderService.say();
        System.out.println("service execute");
    }

}
