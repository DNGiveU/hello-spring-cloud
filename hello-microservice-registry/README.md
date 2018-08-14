### 服务发现&服务治理

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
			
配置先后顺序:
org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration [spring.factories] [客户端配置]
	<- EurekaClientConfigBean (处理eureka.instance属性-客户端相关参数)
	<- EurekaInstanceConfigBean [EurekaInstanceConfig] (处理eureka.instance属性-实例有关属性)
	<- DiscoveryClient (spring的服务发现客户端)
	<- EurekaRegistration (spring的服务注册)
	<- ApplicationInfoManager (实例信息)
	<- EurekaClient [com.netflix.discovery.DiscoveryClient] (*****服务客户端)
EurekaServerInitializerConfiguration (web容器与Eureka服务的结合) [服务端配置]
	-> EurekaServerConfig
	-> EurekaServerBootstrap
	<- EurekaRegistryAvailableEvent
	<- EurekaServerStartedEvent
EurekaServerConfiguration [服务端配置]
	-> ApplicationInfoManager
	-> EurekaServerConfig
	-> EurekaClientConfig
	-> EurekaClient
	<- EurekaServerConfig (如果没有EurekaServerConfig则默认创建一个)
	<- EurekaController
	<- PeerAwareInstanceRegistry
	<- EurekaServerContext
	<- EurekaServerBootstrap
```

### DiscoveryClient

```
*****com.netflix.discovery.DiscoveryClient
	com.netflix.discovery.DiscoveryClient.DiscoveryClient(ApplicationInfoManager, EurekaClientConfig, AbstractDiscoveryClientOptionalArgs, Provider<BackupRegistry>)
```

### EurekaServer

```
EurekaServerContext Eureka上下文可管理InstanceRegistry/PeerEurekaNodes的生命周期以及获取一些组件ApplicationInfoManager/EurekaServerConfig等等
|--DefaultEurekaServerContext

EurekaServerBootstrap Eureka服务的一些设置; 如环境变量/注册一些监控指标信息到Servo

InstanceRegistry Eureka的注册实例管理
|--AbstractInstanceRegistry (Handles all registry requests from eureka clients. Registers/Renewals/Cancels/Expirations/Status Changes)
	节点注册 com.netflix.eureka.registry.AbstractInstanceRegistry.register(InstanceInfo, int, boolean)
	节点删除 com.netflix.eureka.registry.AbstractInstanceRegistry.cancel(String, String, boolean)
	节点更新 com.netflix.eureka.registry.AbstractInstanceRegistry.renew(String, String, boolean)
|----com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl
		集群节点同步 com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl.replicateToPeers(Action, String, String, InstanceInfo, InstanceStatus, boolean)
PeerEurekaNodes Eureka实例管理即集群

Eureka集群间信息批量同步
	com.netflix.eureka.cluster.PeerEurekaNode.PeerEurekaNode(PeerAwareInstanceRegistry, String, String, HttpReplicationClient, EurekaServerConfig, int, long, long, long)
		可有批量任务和非批量任务之分（都是由一个创建器创建com.netflix.eureka.util.batcher.TaskDispatchers）.		
		创建一个批量任务调度器 com.netflix.eureka.util.batcher.TaskDispatchers.createBatchingTaskDispatcher(String, int, int, int, long, long, long, TaskProcessor<T>)
			创建一个接受执行器（接受任务process(ID, T, long)） com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorExecutor<ID,T>(String, int, int, long, long, long)
				启动一个名为TaskAcceptor-的线程com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.AcceptorRunner()归属为eurekaTaskExecutors线程组 [*****这个线程区分或者说是分配了(批量)任务即任务的划分, 如何为一个批量的*****](maxElementsInPeerReplicationPool确定可以缓存多少个任务)
					AcceptorRunner线程run() com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.run()
						com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.drainInputQueues()
							处理需要重新处理的任务 com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.drainReprocessQueue()
							处理接受队列 com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.drainAcceptorQueue()
							...
							添加单个任务(singleItemWorkQueue) com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.assignSingleItemWork()
							添加批量任务(batchWorkQueue) com.netflix.eureka.util.batcher.AcceptorExecutor.AcceptorRunner.assignBatchWork()
			创建一个批量执行器 com.netflix.eureka.util.batcher.TaskExecutors.batchExecutors(String, int, TaskProcessor<T>, AcceptorExecutor<ID, T>)
				创建一个名为eurekaTaskExecutors的线程组(数量由maxThreadsForPeerReplication确定)以守护线程方式运行 com.netflix.eureka.util.batcher.TaskExecutors.TaskExecutors(WorkerRunnableFactory<ID, T>, int, AtomicBoolean)
				线程组中的(TaskBatchingWorker 或者 TaskNonBatchingWorker)线程为 com.netflix.eureka.util.batcher.TaskExecutors.BatchWorkerRunnable.BatchWorkerRunnable<ID,T>(String, AtomicBoolean, TaskExecutorMetrics, TaskProcessor<T>, AcceptorExecutor<ID, T>)
					线程run com.netflix.eureka.util.batcher.TaskExecutors.BatchWorkerRunnable.run()	
						获取批量任务(如果没有关闭工作处理并且获取的处理队列为空，则会循环获取) com.netflix.eureka.util.batcher.TaskExecutors.BatchWorkerRunnable.getWork()
							BlockingQueue<List<TaskHolder<ID, T>>> workQueue = taskDispatcher.requestWorkItems(); [*****requestWorkItems()*****]
						处理批量任务 com.netflix.eureka.cluster.ReplicationTaskProcessor.process(List<ReplicationTask>)
							调用请求发送，处理返回结果
							调用Task的成功回调或者失败回调方法。com.netflix.eureka.cluster.ReplicationTaskProcessor.handleBatchResponse(ReplicationTask, ReplicationInstanceResponse)
							发送批量请求 com.netflix.eureka.transport.JerseyReplicationClient.submitBatchUpdates(ReplicationList)
	
	给AcceptorExecutor处理任务 com.netflix.eureka.util.batcher.AcceptorExecutor.process(ID, T, long)
		LinkBlockingQueue 来接受任务并使acceptedTasks自增 (任务不断由名为TaskAcceptor-的AcceptorRunner线程来处理)
		
Eureka服务对外API (Jersey)
	com.netflix.eureka.resources.ApplicationsResource (获取)
	com.netflix.eureka.resources.ApplicationResource (获取、注册)
		com.netflix.eureka.resources.ApplicationResource.addInstance(InstanceInfo, String)
	com.netflix.eureka.resources.InstanceResource (更新、删除)
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