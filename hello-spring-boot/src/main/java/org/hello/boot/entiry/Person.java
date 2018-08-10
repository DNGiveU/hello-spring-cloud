package org.hello.boot.entiry;

/**
 *
 * @author: hanqiang
 * @Date: 2018年8月10日
 */
public class Person {
	private String name;
	
	public Person() {}
	
	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
