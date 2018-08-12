> 参考文档: http://cloud.spring.io/spring-cloud-static/Finchley.RELEASE/single/spring-cloud.html

## Spring注解

```
@PropertySource
	加载一个属性源文件到环境中即加载一个properties配置文件到Spring的Environment中
@EnableConfigurationProperties	[boot]
	对@ConfigurationProperties支持。即开启@ConfigurationProperties
	如果@ConfigrationProperties中没有值，则只会注册一个ConfigurationPropertiesBindingPostProcessor后置处理器(将环境属性绑定到@ConfigurationProperties注解的类中)。
	如果有值，会将该值与类的权限定名以“-”连接作为key把类注册到SpringIOC中并解析赋值其中的字段，当然后也会注册ConfigurationPropertiesBindingPostProcessor后置处理器
	e.g. @ConfigurationProperties("myBean") => 注册：key: myBean-com.gaz.XX value: com.gaz.XX
	即有@ConfigurationProperties值就会注册当前Bean
@ConfigurationProperties [boot]
	将配置文件中或者环境中的属性值设置到被@ConfigurationProperties修饰的类上(这个过程由@EnableConfigurationProperties注册的ConfigurationPropertiesBindingPostProcessor处理)
	org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor.postProcessBeforeInitialization(Object, String, ConfigurationProperties) 配置属性工厂初始化参数(绑定目标Target类)PropertiesConfigurationFactory
		org.springframework.boot.bind.PropertiesConfigurationFactory.bindPropertiesToTarget()
			org.springframework.boot.bind.PropertiesConfigurationFactory.doBindPropertiesToTarget() 绑定属性到类字段中
			...
			org.springframework.beans.BeanWrapperImpl.BeanPropertyHandler.setValue(Object, Object)
@Configuration
	org.springframework.context.annotation.ConfigurationClassPostProcessor后置处理器
	org.springframework.context.annotation.ConfigurationClassPostProcessor.processConfigBeanDefinitions(BeanDefinitionRegistry)
		org.springframework.context.annotation.ConfigurationClassParser.parse(Set<BeanDefinitionHolder>)
			org.springframework.context.annotation.ConfigurationClassParser.parse(AnnotationMetadata, String)
				org.springframework.context.annotation.ConfigurationClassParser.processConfigurationClass(ConfigurationClass)
					org.springframework.context.annotation.ConfigurationClassParser.doProcessConfigurationClass(ConfigurationClass, SourceClass)
						org.springframework.context.annotation.ConfigurationClassParser.processPropertySource(AnnotationAttributes)
							处理@PropertySource，先由Environment解析其中的变量值(${...})，String resolvedLocation = this.environment.resolveRequiredPlaceholders(location);
							然后由ResourceLoader加载一个资源Resource，
							所加载的资源Resource被PropertySourceFactory封装为一个PropertySource被添加到环境中((ConfigurableEnvironment) this.environment).getPropertySources().addLast(propertySource)
					org.springframework.context.annotation.ComponentScanAnnotationParser.parse(AnnotationAttributes, String)
						处理@ComponentScan注解，解析基本信息并设置到org.springframework.context.annotation.ClassPathBeanDefinitionScanner
					org.springframework.context.annotation.ClassPathBeanDefinitionScanner.doScan(String...)完成扫描加载工作
					org.springframework.context.annotation.ConfigurationClassParser.processImports(ConfigurationClass, SourceClass, Collection<SourceClass>, boolean)
						处理@Import注解
					org.springframework.context.annotation.ConfigurationClassParser.processImports(ConfigurationClass, SourceClass, Collection<SourceClass>, boolean)
						先判断类是何种类型(ImportSelector/ImportBeanDefinitionRegistrar/普通类)再根据对应策略处理
	处理@Bean List<BeanMethod>; org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader.loadBeanDefinitionsForBeanMethod(BeanMethod)
		将注解为@Bean的方法按工厂方法进行处理
		如果@Bean的name属性为空, 则去方法名; String beanName = (!names.isEmpty() ? names.remove(0) : methodName);
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
			SpringApplicationRunListener=>主要是EventPublishingRunListener(传入了SpringApplication, 可访问监听器集合)
		创建一个ConfigurableEnvironment(设置profile、如果是web环境将转换environment为标准环境对象)并执行SpringApplicationRunListener#environmentPrepared(Environment)方法(TODO 哪些执行了什么内容)
			EventPublishingRunListener发布了ApplicationEnvironmentPreparedEvent事件
				FileEncodingApplicationListener处理事件
				ConfigFileApplicationListener处理事件
		创建SpringIOC如果是web环境则创建org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext，否则是AnnotationConfigAppliactionContext(两个类都初始化了AnnotatedBeanDefinitionReader与ClassPathBeanDefinitionScanner)
		org.springframework.boot.SpringApplication.prepareContext(ConfigurableApplicationContext, ConfigurableEnvironment, SpringApplicationRunListeners, ApplicationArguments, Banner) 上下文准备工作; 前面只是创建
			设置环境 context.setEnvironment(environment);
			postProcessApplicationContext(context); 设置beanName生成器、设置资源加载器
			applyInitializers(context); 调用initializers集合中元素的initialize方法进行元素初始化 (TODO 哪些执行了什么内容)
			listeners.contextPrepared(context); 上下文准备监听; 监听器做准备 (TODO 哪些执行了什么内容)
				EventPublishingRunListener#contextPrepared(ConfigurableApplicationContext) 为空
			注册springApplicationArguments、springBootBanner
			load(context, sources.toArray(new Object[sources.size()])); 将启动类注册到SpringIOC中
				封装一个BeanDefinitionLoader去解析启动类  *******************重点*****************
					org.springframework.boot.BeanDefinitionLoader.load(Object)
						org.springframework.boot.BeanDefinitionLoader.load(Class<?>)
							org.springframework.context.annotation.AnnotatedBeanDefinitionReader.registerBean(Class<?>, String, Class<? extends Annotation>...)
								org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)
			listeners.contextLoaded(context); 上下文加载完成监听; 监听器做准备 (TODO 哪些执行了什么内容)
				org.springframework.boot.context.event.EventPublishingRunListener.contextLoaded(ConfigurableApplicationContext) 为springIOC添加初始化的监听器(SpringApplcation中的监听器)
				广播ApplicationPreparedEvent事件
		refreshContext(context); 刷新容器; 来到我们熟悉的((AbstractApplicationContext) applicationContext).refresh(); 完成一些列的beanPostProcessor/Bean的解析与创建并发布事件
		afterRefresh(context, applicationArguments); 触发容器中的ApplicationRunner/CommandLineRunner
		listeners.finished(context, null); Run监听器结束方法触发 (TODO 哪些执行了什么内容)
			广播ApplicationReadyEvent事件
		stopWatch.stop(); 统计信息
```

```Java
初始化的监听器所对应的处理事件
ClearCachesApplicationListener-ContextRefreshedEvent
	清空ReflectionUtils的缓存以及类加载器的缓存
ParentContextCloserApplicationListener-ParentContextAvailableEvent
	向父容器注册一个拥有子容器的ContextCloserListener监听器以此来关闭子容器(父容器关闭时会广播上下文关闭事件ContextClosedEvent)
FileEncodingApplicationListener-ApplicationEnvironmentPreparedEvent
	校准spring上下文强制指定的文件编码; 如果系统不支持则抛出异常; spring.mandatoryFileEncoding属性
AnsiOutputApplicationListener-ApplicationEnvironmentPreparedEvent
	检测环境中spring.output.ansi.enabled的值是否为true以此来开启AnsiOutput
ConfigFileApplicationListener-ApplicationEnvironmentPreparedEvent/ApplicationPreparedEvent
	ApplicationEnvironmentPreparedEvent 获取容器中EnvironmentPostProcessor后置处理器处理Environment*****
		ConfigFileApplicationListener(implements EnvironmentPostProcessor)#postProcessEnvironment(ConfigurableEnvironment, SpringApplication)
			org.springframework.boot.context.config.ConfigFileApplicationListener.addPropertySources(ConfigurableEnvironment, ResourceLoader)
				注册一个随机数属性源到环境中new RandomValuePropertySource(RANDOM_PROPERTY_SOURCE_NAME="random")
				new Loader(environment, resourceLoader).load();
				加载名为applicatioin的文件, 后缀名策略根据SpringFactories加载的org.springframework.boot.env.PropertySourceLoader决定 (PropertiesPropertySourceLoader/YamlPropertySourceLoader都加载); 即加载application.yaml/application.yml/application.properties/application.xml
			org.springframework.boot.context.config.ConfigFileApplicationListener.configureIgnoreBeanInfo(ConfigurableEnvironment) 检测spring.beaninfo.ignore属性以及是否需要设置到System属性中
			org.springframework.boot.context.config.ConfigFileApplicationListener.bindToSpringApplication(ConfigurableEnvironment, SpringApplication) 从环境中绑定spring.main到SpringApplication中
		SpringApplicationJsonEnvironmentPostProcessor
			获取环境中spring.application.json将其解析为Map属性源添加到环境中
			e.g. spring.application.json={key: value} 处理后可通过environment.getProperty("key")获取值
		HostInfoEnvironmentPostProcessor
			将客户端的主机信息(ip/hostname)添加到环境中
				"spring.cloud.client.hostname", hostInfo.getHostname()
				"spring.cloud.client.ipAddress", hostInfo.getIpAddress()
	ApplicationPreparedEvent 向容器中注册一个工厂后置处理器PropertySourceOrderingPostProcessor
DelegatingApplicationListener-ApplicationEvent
	委托监听组合器；即维持一个成员变量来存储SmartApplicationListener集合；然后通过onApplicationEvent统一触发
LiquibaseServiceLocatorApplicationListener-ApplicationStartedEvent
	检测是否有liquibase.servicelocator.ServiceLocator类；如果有则ServiceLocator.setInstance(new CustomResolverServiceLocator(new SpringPackageScanClassResolver(logger)));
ClasspathLoggingApplicationListener-ApplicationEnvironmentPreparedEvent/ApplicationFailedEvent
	输出日志
	ApplicationEnvironmentPreparedEvent-this.logger.debug("Application started with classpath: " + getClasspath());
	ApplicationFailedEvent-this.logger.debug("Application failed to start with classpath: " + getClasspath());
LoggingApplicationListener-ApplicationStartedEvent/ApplicationEnvironmentPreparedEvent/ApplicationPreparedEvent/ContextClosedEvent
	ApplicationStartedEvent-初始化一个日志系统loggingSystem
	ApplicationEnvironmentPreparedEvent-初始化日志系统的相关参数(参数从LogFile或者Environment中获取)
	ApplicationPreparedEvent-注册一个日志系统bean的key为LOGGING_SYSTEM_BEAN_NAME=springBootLoggingSystem
	ContextClosedEvent-日志系统cleanUp
BackgroundPreinitializer-ApplicationEnvironmentPreparedEvent
	开启一个线程来过早初始化一些类; AllEncompassingFormHttpMessageConverter/MBeanFactory/Validation.byDefaultProvider().configure()/Jackson2ObjectMapperBuilder.json().build()/DefaultFormattingConversionService
```

```Java
spring-boot-xx.jar
# PropertySource Loaders
org.springframework.boot.env.PropertySourceLoader=\
org.springframework.boot.env.PropertiesPropertySourceLoader,\
org.springframework.boot.env.YamlPropertySourceLoader

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