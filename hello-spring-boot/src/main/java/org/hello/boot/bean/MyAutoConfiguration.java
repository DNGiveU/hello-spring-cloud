package org.hello.boot.bean;

import org.hello.boot.annotation.LinuxConditional;
import org.hello.boot.annotation.WindowConditional;
import org.hello.boot.entiry.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
@Configuration
public class MyAutoConfiguration {

	@Bean
	@WindowConditional
	public Person gaz() {
		return new Person("gaz");
	}
	
	@Bean
	@LinuxConditional
	public Person linus() {
		return new Person("linus");
	}
}
