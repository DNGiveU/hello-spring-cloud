package org.hello.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hello.boot.condition.WindowCondition;
import org.springframework.context.annotation.Conditional;

/**
 * 判断是否是window环境
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Conditional(WindowCondition.class)
public @interface WindowConditional {

}
