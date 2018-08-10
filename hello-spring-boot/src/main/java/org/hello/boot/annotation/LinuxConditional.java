package org.hello.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hello.boot.condition.LinuxCondition;
import org.springframework.context.annotation.Conditional;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Conditional(LinuxCondition.class)
public @interface LinuxConditional {

}
