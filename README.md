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
```