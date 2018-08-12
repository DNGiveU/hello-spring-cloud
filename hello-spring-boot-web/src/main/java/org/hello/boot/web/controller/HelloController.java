package org.hello.boot.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author gaz
 * @date 2018年8月12日
 */
@RestController
public class HelloController {

	@GetMapping("/hello")
	public String hello() {
		return "Hello, I'm print 'Hello'.";
	}
}
