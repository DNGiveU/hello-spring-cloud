## SpringBoot&&Tomcat

> 探索spring-boot的内嵌web容器过程

```
org.springframework.boot.context.embedded.EmbeddedWebApplicationContext
	|--org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext
	org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.onRefresh() 
		创建一个内置的web容器并Spring容器/环境设置到web容器中; (通过注册在容器中的内嵌Servlet容器工厂 org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration)
			如注册一个org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory.TomcatEmbeddedServletContainerFactory() Bean
				获取一个内嵌的Tomcat容器(TomcatEmbeddedServletContainer的构造方法中启动Tomcat容器) org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory.getEmbeddedServletContainer(ServletContextInitializer...)
					创建一个Tomcat容器
					设置Tomcat的服务的连接器; tomcat.getService().addConnector(connector);
					设置Tomcat的连接器; tomcat.setConnector(connector);
					设置Tomcat的Host参数; tomcat.getHost().setAutoDeploy(false);
					设置Tomcat的Engine配置; configureEngine(tomcat.getEngine());
					创建并返回一个TomcatEmbeddedServletContainer; return new TomcatEmbeddedServletContainer(tomcat, getPort() >= 0);
						org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer.initialize()
							设置Tomcat的Engine名称; addInstanceIdToEngineName();
							移除Tomcat的Service中连接器放置在Map#serviceConnectors中 (在调用TomcatEmbeddedServletContainer#start()方法时,会重新设置到Tomcat的Service中); removeServiceConnectors()
							启动Tomcat容器; this.tomcat.start();
							抛出Tomcat启动时的异常; rethrowDeferredStartupExceptions()
							创建一个阻塞式的非守护线程去停止立即关闭JVM; startDaemonAwaitThread();
								Thread awaitThread = new Thread("container-" + (containerCounter.get())) {
									@Override
									public void run() {
										TomcatEmbeddedServletContainer.this.tomcat.getServer().await();
									}
								};
	org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.finishRefresh()
		调用org.springframework.boot.context.embedded.EmbeddedServletContainer的start()方法
			org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer.start()
				将serviceConnectors中的连接器设置到Tomcat的Service中; addPreviouslyRemovedConnectors()
				启动内嵌的Tomcat上下文; startConnector(connector);
					org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedContext.deferredLoadOnStartup()
						主要将Tomcat的类加载器设置到线程上下文类加载器中; 注释中也解释了: 一些老的Servlet框架(Struts, BIRT)使用线程上下文类加载器来加载Servlet. (用Tomcat的累加器去加载所有的Servlet, 便于热加载时处理)
				检查Tomcat中的连接器的启动是否成功; checkThatConnectorsHaveStarted();
```