package org.hello.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Hello world!
 */
@SpringBootApplication
public class Application {
	
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        System.out.println("**********Spring Boot流程完成**********");
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
        	System.out.println(beanName);
        }
        System.out.println("=====自定义属性 application.my");
        System.out.println("name=" + applicationContext.getEnvironment().getProperty("name"));
        System.out.println("**********Spring Boot流程完成**********EOF");
    }
}
