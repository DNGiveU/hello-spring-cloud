package org.hello.api;

/**
 * 
 * @author gaz
 * @date 2018年8月14日
 */
public class User {
	private Long id;
	private String username;
	private String age;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
}
