> 参考文档: http://cloud.spring.io/spring-cloud-static/Finchley.RELEASE/single/spring-cloud.html#_the_bootstrap_application_context

## Spring注解

```
@PropertySource
	加载一个属性源文件到环境中即加载一个properties配置文件到Spring的Environment中
@EnableConfigurationProperties	[boot]
	对@ConfigurationProperties支持。即开启@ConfigurationProperties
	如果@ConfigrationProperties中没有值，则只会注册一个ConfigurationPropertiesBindingPostProcessor后置处理器(将环境属性绑定到@ConfigurationProperties注解的类中)。
	如果有值，会将该值与类的权限定名以“-”连接作为key把类注册到SpringIOC中，当然后也会注册ConfigurationPropertiesBindingPostProcessor后置处理器
	e.g. @ConfigurationProperties("myBean") => 注册：key: myBean-com.gaz.XX value: com.gaz.XX
	即有@ConfigurationProperties值就会注册当前Bean
@ConfigurationProperties [boot]
	将配置文件中或者环境中的属性值设置到被@ConfigurationProperties修饰的类上(这个过程由@EnableConfigurationProperties注册的ConfigurationPropertiesBindingPostProcessor处理)
	org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor.postProcessBeforeInitialization(Object, String, ConfigurationProperties) 配置属性工厂初始化参数(绑定目标Target类)PropertiesConfigurationFactory
		org.springframework.boot.bind.PropertiesConfigurationFactory.bindPropertiesToTarget()
			org.springframework.boot.bind.PropertiesConfigurationFactory.doBindPropertiesToTarget() 绑定属性到类字段中
			...
			org.springframework.beans.BeanWrapperImpl.BeanPropertyHandler.setValue(Object, Object)
@SpringBootApplication
	此注解等价于@Configuration/@EnableAutoConfiguration/@ComponentScan
	@SpringBootConfiguration 相当于是一个Configuration配置类
	@EnableAutoConfiguration 使用SpringFactoriesLoader机制来自动配置Bean(@Conditional/@ConditionalOnBean/@ConditionalOnMissingBean)以及扫描子包
		@AutoConfigurationPackage
			注册一个key为AutoConfigurationPackages.class.getName()的BasePackages类去持有(Holder)一个基本的包路径(默认为当前注解修饰的类的包路径)
		@Import(EnableAutoConfigurationImportSelector.class)
			通过SpringFactoriesLoader机制加载org.springframework.boot.autoconfigure.EnableAutoConfiguration为key的值到SpringIOC(spring-boot-autoconfigure-xx.jar中)并通过ConditionEvaluationReport记录加载的类名以及排除的类名
			spring.factories加载的类见下面：# Auto Configure
	@ComponentScan(excludeFilters = @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class)) 自定义过滤机制即获取容器中的TypeExcludeFilter去执行其中的match方法。由于没有指定value，会将当前包作为value来执行扫描。If specific packages are not defined, scanning will occur from the package of the class that declares this annotation.
org.springframework.boot.SpringApplication.run(Xxx.class, args) 启动
	org.springframework.boot.SpringApplication.initialize(Object[])
		Spring初始化参数；
		1. 检查是否是web环境(检查javax.servlet.Servlet/org.springframework.web.context.ConfigurableWebApplicationContext是否存在)
		2. 通过 SpringFactories加载org.springframework.context.ApplicationContextInitializer的key的值并实例化添加到SpringApplication的initializers集合当中
		3. 通过SpringFactories加载org.springframework.context.ApplicationListener的key的值并实例化添加到SpringApplication的listeners集合当中
		4. 获取启动类(即持有main方法的类); StackTraceElement[] stackTrace = new RuntimeException().getStackTrace(); 遍历比较main方法的类
	org.springframework.boot.SpringApplication.run(String...) 创建并刷新一个Spring容器(SpringApplication)
		开启一个StopWatch记录运行时间以及任务运行时间等
		初始化一个SpringApplicationRunListeners类(其通过SpringFactories机制加载了key为org.springframework.boot.SpringApplicationRunListener的类并设置到了其listeners属性当中)来作为一个初始的事件监听组合类，便于统一管理(启动、销毁、发布时间等)
		创建一个ConfigurableEnvironment(设置profile、如果是web环境将转换environment为标准环境对象)并执行SpringApplicationRunListener#environmentPrepared(Environment)方法(TODO 哪些执行了什么内容)
		创建SpringIOC如果是web环境则创建org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext，否则是AnnotationConfigAppliactionContext(两个类都初始化了AnnotatedBeanDefinitionReader与ClassPathBeanDefinitionScanner)
		org.springframework.boot.SpringApplication.prepareContext(ConfigurableApplicationContext, ConfigurableEnvironment, SpringApplicationRunListeners, ApplicationArguments, Banner) 上下文准备工作; 前面只是创建
			设置环境 context.setEnvironment(environment);
			postProcessApplicationContext(context); 设置beanName生成器、设置资源加载器
			applyInitializers(context); 调用initializers集合中元素的initialize方法进行元素初始化 (TODO 哪些执行了什么内容)
			listeners.contextPrepared(context); 上下文准备监听; 监听器做准备 (TODO 哪些执行了什么内容)
			注册springApplicationArguments、springBootBanner
			load(context, sources.toArray(new Object[sources.size()])); 将启动类注册到SpringIOC中
				封装一个BeanDefinitionLoader去解析启动类  *******************重点*****************
					org.springframework.boot.BeanDefinitionLoader.load(Object)
						org.springframework.boot.BeanDefinitionLoader.load(Class<?>)
							org.springframework.context.annotation.AnnotatedBeanDefinitionReader.registerBean(Class<?>, String, Class<? extends Annotation>...)
								org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)
			listeners.contextLoaded(context); 上下文加载完成监听; 监听器做准备 (TODO 哪些执行了什么内容)
		refreshContext(context); 刷新容器; 来到我们熟悉的((AbstractApplicationContext) applicationContext).refresh(); 完成一些列的beanPostProcessor/Bean的解析与创建并发布事件
		afterRefresh(context, applicationArguments); 触发容器中的ApplicationRunner/CommandLineRunner
		listeners.finished(context, null); 监听器结束方法触发
		stopWatch.stop(); 统计信息
```

```Java
spring-boot-xx.jar
# Run Listeners
org.springframework.boot.SpringApplicationRunListener=\
org.springframework.boot.context.event.EventPublishingRunListener

# Application Context Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
org.springframework.boot.context.ContextIdApplicationContextInitializer,\
org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
org.springframework.boot.context.web.ServerPortInfoApplicationContextInitializer

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener,\
org.springframework.boot.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.logging.LoggingApplicationListener

spring-boot-autoconfigure-xx.jar
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.AutoConfigurationReportLoggingInitializer

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer

# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration,\
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration,\
org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration,\
org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration,\
org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration,\
org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration,\
org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration,\
org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration,\
org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration,\
org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration,\
org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration,\
org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration,\
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration,\
org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.hornetq.HornetQAutoConfiguration,\
org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,\
org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration,\
org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration,\
org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration,\
org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration,\
org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration,\
org.springframework.boot.autoconfigure.mobile.DeviceResolverAutoConfiguration,\
org.springframework.boot.autoconfigure.mobile.DeviceDelegatingViewResolverAutoConfiguration,\
org.springframework.boot.autoconfigure.mobile.SitePreferenceAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,\
org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration,\
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration,\
org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration,\
org.springframework.boot.autoconfigure.security.FallbackWebSecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration,\
org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration,\
org.springframework.boot.autoconfigure.session.SessionAutoConfiguration,\
org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration,\
org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration,\
org.springframework.boot.autoconfigure.social.LinkedInAutoConfiguration,\
org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration,\
org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration,\
org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration,\
org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration,\
org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration,\
org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration,\
org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration,\
org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration,\
org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration,\
org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration,\
org.springframework.boot.autoconfigure.web.WebClientAutoConfiguration,\
org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.WebSocketMessagingAutoConfiguration,\
org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration

spring-cloud-context-xx.jar
# AutoConfiguration
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration,\
org.springframework.cloud.autoconfigure.RefreshAutoConfiguration,\
org.springframework.cloud.autoconfigure.RefreshEndpointAutoConfiguration,\
org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.cloud.bootstrap.BootstrapApplicationListener,\
org.springframework.cloud.context.restart.RestartListener
```