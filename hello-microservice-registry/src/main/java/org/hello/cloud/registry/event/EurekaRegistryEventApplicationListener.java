package org.hello.cloud.registry.event;

import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.context.ApplicationListener;

import com.netflix.eureka.EurekaServerConfig;

/**
 * 服务注册事件
 * @author gaz	
 * @date 2018年8月26日
 */
public class EurekaRegistryEventApplicationListener implements ApplicationListener<EurekaRegistryAvailableEvent> {

	@Override
	public void onApplicationEvent(EurekaRegistryAvailableEvent event) {
		if (event.getSource() instanceof EurekaServerConfig) {
			EurekaServerConfig eurekaServiceConfig = (EurekaServerConfig) event.getSource();
			System.out.println("==============EurekaRegistryAvailableEvent >>>start>>> ================ ");
			System.out.println(eurekaServiceConfig);
			System.out.println("==============EurekaRegistryAvailableEvent >>>end>>> ================ ");
		}
	}
}
