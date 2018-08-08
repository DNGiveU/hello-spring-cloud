package org.hello.cloud.registry;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月8日
 */
@ConfigurationProperties("spring.application")
public class ConfigurationPropertiesBean {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
