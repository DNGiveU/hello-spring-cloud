package org.hello.boot.bean;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 可根据事件类型进行处理。 
 * 事件类型									-	事件数据
 * ApplicationStartedEvent					-	SpringApplication
 * ApplicationEnvironmentPreparedEvent		-	SpringApplication
 * ApplicationPreparedEvent					-	SpringApplication
 * ContextRefreshedEvent					-	AnnotationConfigApplicationContext
 * ApplicationReadyEvent					-	SpringApplication
 * ContextClosedEvent						-	AnnotationConfigApplicationContext
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		System.out.println("==========事件处理==========");
		System.out.println("事件类型type=" + event.getClass().getName());
		System.out.println("事件数据data=" + event.getSource().getClass().getName());
		System.out.println("==========事件处理==========EOF");
	}
}
