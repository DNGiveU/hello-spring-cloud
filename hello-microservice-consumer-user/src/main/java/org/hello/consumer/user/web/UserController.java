package org.hello.consumer.user.web;

import org.hello.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author gaz
 * @date 2018年8月26日
 */
@Controller
public class UserController {

	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/user/{id}")
	public User findUser(@PathVariable int id) {
		return this.restTemplate.getForObject("http://MICROSERVICE-PROVIDER-USER/user/{id}", User.class, id);
	}
}
