package org.hello.cloud.registry;

import org.hello.cloud.registry.event.EurekaRegistryEventApplicationListener;
import org.hello.cloud.registry.event.EurekaStartApplicationEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 * @author gaz
 * @date 2018-8-7 23:49:29
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
    
    // 如果不行，则写入到spring.factories中
    @Bean
    public EurekaRegistryEventApplicationListener eurekaRegistryEventApplicationListener() {
    	return new EurekaRegistryEventApplicationListener();
    }
    
    @Bean
    public EurekaStartApplicationEventListener eurekaStartApplicationEventListener() {
    	return new EurekaStartApplicationEventListener();
    }
}
