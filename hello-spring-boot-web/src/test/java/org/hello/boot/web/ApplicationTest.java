package org.hello.boot.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {

//	@Test
//	public void startEmbedTomcat() throws IOException {
	public static void main(String[] args) throws IOException {
		EmbeddedServletContainerFactory embeddedServletContainerFactory = new TomcatEmbeddedServletContainerFactory();
		
		/**
		 * 输入: http://localhost:8080/hello
		 */
		EmbeddedServletContainer embeddedServletContainer = embeddedServletContainerFactory.getEmbeddedServletContainer(new ServletContextInitializer() {
			
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.addServlet("hello", new HttpServlet() {
					
					private static final long serialVersionUID = 4954655362231610216L;

					@Override
					protected void doGet(HttpServletRequest req, HttpServletResponse resp)
							throws ServletException, IOException {
						doPost(req, resp);
					}
					
					@Override
					protected void doPost(HttpServletRequest req, HttpServletResponse resp)
							throws ServletException, IOException {
						resp.getWriter().println("Hello, Embed Tomcat.");
						resp.getWriter().flush();
					}
				}).addMapping("/hello");
			}
		});
		embeddedServletContainer.start();
		
		// 主线程等待 使用@Test时需要
//		System.in.read();
	}
}
