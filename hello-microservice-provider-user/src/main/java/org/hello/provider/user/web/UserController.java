package org.hello.provider.user.web;

import org.hello.provider.user.domain.User;
import org.hello.provider.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author gaz
 * @date 2018年8月14日
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@GetMapping("{id}")
	public User findById(@PathVariable Long id) {
		return this.userRepository.findOne(id);
	}
	
	@GetMapping("/instanceInfo")
	public ServiceInstance findInfo() {
		ServiceInstance localServiceInstance = this.discoveryClient.getLocalServiceInstance();
		return localServiceInstance;
	}
}
