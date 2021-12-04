package org.springframework.config;

/**
 * bean定义类
 *
 * @author endwas
 * @date Created in 2021/12/4 10:07
 */

public class BeanDefinition {
    Class<?> clazz;
    String scope;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
