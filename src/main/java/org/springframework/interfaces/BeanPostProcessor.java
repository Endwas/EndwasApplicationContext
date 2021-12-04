package org.springframework.interfaces;

/**
 * bean后置处理器
 *
 * @author ：endwas
 * @date ：Created in 2021/12/4 9:39
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);
}
