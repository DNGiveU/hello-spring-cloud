package org.hello.cloud.registry;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月8日
 */
@Configurable
@PropertySource("classpath:application.properties")
//@EnableConfigurationProperties(value = {EnableConfigurationPropertiesBean.class})
@EnableConfigurationProperties({ConfigurationPropertiesBean.class})
public class ConfigBean {

}
