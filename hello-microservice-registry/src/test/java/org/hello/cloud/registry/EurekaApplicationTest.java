package org.hello.cloud.registry;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class EurekaApplicationTest {
	
	@Test
	public void testApplicationContext() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConfigBean.class);
		for (String name : applicationContext.getBeanDefinitionNames()) {
			System.out.println(name);
		}
		ConfigurationPropertiesBean configurationPropertiesBean = applicationContext.getBean(ConfigurationPropertiesBean.class);
		System.out.println(configurationPropertiesBean.getName());
//		System.out.println(applicationContext.getEnvironment().getProperty("spring.application.name"));
	}
}
