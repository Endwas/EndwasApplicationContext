package org.springframework.interfaces;

/**
 * 初始化bean接口
 *
 * @author endwas
 * @date Created in 2021/12/4 9:38
 */
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
