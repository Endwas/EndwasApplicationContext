package org.springframework.annotation;


import java.lang.annotation.*;

/**
 * @author endwas
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PostConstruct {
}
