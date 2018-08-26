package org.hello.cloud.registry.event;

import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.ApplicationListener;

import com.netflix.eureka.EurekaServerConfig;

/**
 * Eureka服务启动
 * @author gaz
 * @date 2018年8月26日
 */
public class EurekaStartApplicationEventListener implements ApplicationListener<EurekaServerStartedEvent> {

	@Override
	public void onApplicationEvent(EurekaServerStartedEvent event) {
		EurekaServerConfig eurekaServerConfig = (EurekaServerConfig) event.getSource();
		
		System.out.println("==============EurekaServerStartedEvent >>>start>>> ================ ");
		System.out.println(eurekaServerConfig);
		System.out.println("==============EurekaServerStartedEvent >>>end>>> ================ ");
	}
}
