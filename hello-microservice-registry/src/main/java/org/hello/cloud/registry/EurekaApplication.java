package org.hello.cloud.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Hello world!
 * @author gaz
 * @date 2018-8-7 23:49:29
 */
//@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
