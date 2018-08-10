package org.hello.boot.bean;

import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
public class MyPropertySourceLoader implements PropertySourceLoader {

	/**
	 * 可加载application.my
	 */
	@Override
	public String[] getFileExtensions() {
		return new String[] {"my"};
	}

	/**
	 * 借鉴PropertiesPropertySourceLoader的写法
	 */
	@Override
	public PropertySource<?> load(String name, Resource resource, String profile) throws IOException {
		if (profile == null) {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			if (!properties.isEmpty()) {
				return new PropertiesPropertySource(name, properties);
			}
		}
		return null;
	}
}
