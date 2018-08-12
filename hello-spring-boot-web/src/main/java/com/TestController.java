package com;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问不到, 因为@SpringBootApplication 默认策略是扫描所在类的包及以下子包的
 * 
 * @author gaz
 * @date 2018年8月12日
 */
@RestController
public class TestController {

	@GetMapping("/test")
	public String test() {
		return "Hello, I'm test interface.";
	}
}
