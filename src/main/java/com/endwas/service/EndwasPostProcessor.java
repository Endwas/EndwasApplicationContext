package com.endwas.service;

import org.springframework.annotation.Component;
import org.springframework.interfaces.BeanPostProcessor;

/**
 * bean自定义后置处理器
 *
 * @author endwas
 * @date Created in 2021/12/4 14:31
 */
@Component
public class EndwasPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("endwas postProcessBeforeInitialization : [" + beanName + "]");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("endwas postProcessAfterInitialization : [" + beanName + "]");
        return bean;
    }
}
