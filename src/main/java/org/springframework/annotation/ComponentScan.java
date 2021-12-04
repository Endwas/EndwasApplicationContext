package org.springframework.annotation;


import java.lang.annotation.*;

/**
 * @author endwas
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScan {
    String[] value() default {};
}
