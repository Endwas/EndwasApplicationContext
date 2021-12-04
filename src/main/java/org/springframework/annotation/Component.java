package org.springframework.annotation;

import java.lang.annotation.*;

/**
 * 组件接口
 *
 * @author ：endwas
 * @date ：Created in 2021/12/4 10:11
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Component {
    String value() default "";
}
