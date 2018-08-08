### @EnableEurekaServer做了什么

```
@EnableEurekaServer
	导入了org.springframework.cloud.netflix.eureka.server.EurekaServerConfiguration类
		导入了org.springframework.cloud.netflix.eureka.server.EurekaServerInitializerConfiguration
			实现了org.springframework.context.SmartLifecycle接口 SpringIOC会触发其中的一系列钩子函数(时机: 容器初始化后期)
			主要做了一件事即初始化org.springframework.cloud.netflix.eureka.server.EurekaServerBootstrap
				初始化一些参数； 如数据中心值，环境，json/xml转换器以及将eureka上下文EurekaServerContext放置在EurekaServerContextHolder中
				将eureka参数初始化后放置在J2EE应用的上下文中即ServletContext
				context.setAttribute(EurekaServerContext.class.getName(), this.serverContext);
			并发布了两个事件；一是eureka注册事件EurekaRegistryAvailableEvent，二是eureka服务启动事件EurekaServerStartedEvent
		启动了@EnableDiscoveryClient注解
			使用org.springframework.cloud.client.discovery.EnableDiscoveryClientImportSelector策略注册管理Bean
				SpringFactoryImportSelector#selectImports(AnnotationMetadata)
					使用SpringFactoriesLoader.loadFactoryNames(this.annotationClass, this.beanClassLoader)来Find all possible auto configuration classes, filtering duplicates
					加载并解析META-INF/spring.factories中org.springframework.cloud.client.discovery.EnableDiscoveryClient的值
						在spring-cloud-netflix-eureka-client中发现org.springframework.cloud.client.discovery.EnableDiscoveryClient=org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration
			即@EnableDiscoveryClient导入了EurekaDiscoveryClientConfiguration类
				做了三件事：
					一是配置了Marker类 (作用待定)
					二是配置了一个事件监听类来监听RefreshScopeRefreshedEvent以此来使用EurekaAutoServiceRegistration重新注册eureka服务
					三是配置了一个eureka健康检查处理器EurekaHealthCheckHandler
		注册了org.springframework.cloud.netflix.eureka.server.EurekaDashboardProperties类
			indicate eureka dashboard info. for example The path to the Eureka dashboard
		加载了spring-cloud-netflix-eureka-server下的eureka/server.properties文件
			主要indicate http unforce encoding. spring.http.encoding.force=false
```

### 最小的配置

```yml
server.port=8050
spring.application.name=discovery
eureka.instance.hostname=localhost
eureka.client.serverUrl.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
eureka.client.fetchRegistry=false
eureka.client.registryWithEureka=false
```