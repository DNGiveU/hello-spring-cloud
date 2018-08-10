package org.hello.boot.bean;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 在SpringIOC启动时，可以对容器进行调整以及参数的设置
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
public class MyApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		System.out.println("==========容器中加载的Bean Name==========");
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			System.out.println(beanName);
		}
		System.out.println("==========容器中加载的Bean Name==========EOF");
	}
}
