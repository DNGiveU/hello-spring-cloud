package org.hello.cloud.registry;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月8日
 */
@ConfigurationProperties("custom")
public class EnableConfigurationPropertiesBean {
	
	public EnableConfigurationPropertiesBean() {
		
		int a = 0;
	}

	private String path = "/";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
