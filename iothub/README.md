# iothub
IoT Hub

Make sure that Kafka/Cassandra are running, before starting the IoT Hub:
```
java -jar iothub-1.0.0-boot.jar
```

The output should be shown as below.

```
xis-macbook-pro:target xiningwang$ java -jar iothub-1.0.0-boot.jar
 ===================================================
 :: IoT Hub ::      1.0.0
 ===================================================

2017-08-25 11:18:57.936  INFO 5565 --- [           main] o.i.i.server.IoTHubServerApplication     : Starting IoTHubServerApplication v1.0.0 on xis-macbook-pro.cn.ibm.com with PID 5565 (/Users/xiningwang/localgit/iotplatform/iothub/target/iothub-1.0.0-boot.jar started by xiningwang in /Users/xiningwang/localgit/iotplatform/iothub/target)
2017-08-25 11:18:57.941  INFO 5565 --- [           main] o.i.i.server.IoTHubServerApplication     : No active profile set, falling back to default profiles: default
2017-08-25 11:18:57.984  INFO 5565 --- [           main] ationConfigEmbeddedWebApplicationContext : Refreshing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@1936f0f5: startup date [Fri Aug 25 11:18:57 CST 2017]; root of context hierarchy
2017-08-25 11:18:59.895  INFO 5565 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat initialized with port(s): 8081 (http)
2017-08-25 11:18:59.908  INFO 5565 --- [           main] o.apache.catalina.core.StandardService   : Starting service Tomcat
2017-08-25 11:18:59.909  INFO 5565 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/8.5.6
2017-08-25 11:19:00.010  INFO 5565 --- [ost-startStop-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2017-08-25 11:19:00.010  INFO 5565 --- [ost-startStop-1] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2029 ms
2017-08-25 11:19:00.181  INFO 5565 --- [ost-startStop-1] o.s.b.w.servlet.ServletRegistrationBean  : Mapping servlet: 'dispatcherServlet' to [/]
2017-08-25 11:19:00.185  INFO 5565 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'characterEncodingFilter' to: [/*]
2017-08-25 11:19:00.185  INFO 5565 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'hiddenHttpMethodFilter' to: [/*]
2017-08-25 11:19:00.185  INFO 5565 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'httpPutFormContentFilter' to: [/*]
2017-08-25 11:19:00.185  INFO 5565 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'requestContextFilter' to: [/*]
2017-08-25 11:19:00.243  INFO 5565 --- [           main] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values:
       	compression.type = none
       	metric.reporters = []
       	metadata.max.age.ms = 300000
       	metadata.fetch.timeout.ms = 60000
       	reconnect.backoff.ms = 50
       	sasl.kerberos.ticket.renew.window.factor = 0.8
       	bootstrap.servers = [127.0.0.1:9092]
       	retry.backoff.ms = 100
       	sasl.kerberos.kinit.cmd = /usr/bin/kinit
       	buffer.memory = 33554432
       	timeout.ms = 30000
       	key.serializer = class org.apache.kafka.common.serialization.StringSerializer
       	sasl.kerberos.service.name = null
       	sasl.kerberos.ticket.renew.jitter = 0.05
       	ssl.keystore.type = JKS
       	ssl.trustmanager.algorithm = PKIX
       	block.on.buffer.full = false
       	ssl.key.password = null
       	max.block.ms = 60000
       	sasl.kerberos.min.time.before.relogin = 60000
       	connections.max.idle.ms = 540000
       	ssl.truststore.password = null
       	max.in.flight.requests.per.connection = 5
       	metrics.num.samples = 2
       	client.id =
       	ssl.endpoint.identification.algorithm = null
       	ssl.protocol = TLS
       	request.timeout.ms = 30000
       	ssl.provider = null
       	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
       	acks = -1
       	batch.size = 16384
       	ssl.keystore.location = null
       	receive.buffer.bytes = 32768
       	ssl.cipher.suites = null
       	ssl.truststore.type = JKS
       	security.protocol = PLAINTEXT
       	retries = 0
       	max.request.size = 1048576
       	value.serializer = class org.apache.kafka.common.serialization.StringSerializer
       	ssl.truststore.location = null
       	ssl.keystore.password = null
       	ssl.keymanager.algorithm = SunX509
       	metrics.sample.window.ms = 30000
       	partitioner.class = class org.apache.kafka.clients.producer.internals.DefaultPartitioner
       	send.buffer.bytes = 131072
       	linger.ms = 0

2017-08-25 11:19:00.276  INFO 5565 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version : 0.9.0.0
2017-08-25 11:19:00.276  INFO 5565 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId : fc7243c2af4b2b4a
2017-08-25 11:19:00.276  INFO 5565 --- [           main] o.i.i.server.outbound.kafka.MsgProducer  : Kafka Producer is started....
2017-08-25 11:19:00.541  WARN 5565 --- [           main] com.datastax.driver.core.NettyUtil       : Found Netty's native epoll transport, but not running on linux-based operating system. Using NIO instead.
2017-08-25 11:19:01.002  INFO 5565 --- [           main] c.d.d.c.p.DCAwareRoundRobinPolicy        : Using data-center name 'datacenter1' for DCAwareRoundRobinPolicy (if this is incorrect, please provide the correct datacenter name with DCAwareRoundRobinPolicy constructor)
2017-08-25 11:19:01.004  INFO 5565 --- [           main] com.datastax.driver.core.Cluster         : New Cassandra host /127.0.0.1:9042 added
2017-08-25 11:19:01.197  INFO 5565 --- [           main] o.i.i.server.mqtt.MqttTransportService   : Setting resource leak detector level to DISABLED
2017-08-25 11:19:01.197  INFO 5565 --- [           main] o.i.i.server.mqtt.MqttTransportService   : Starting MQTT transport...
2017-08-25 11:19:01.197  INFO 5565 --- [           main] o.i.i.server.mqtt.MqttTransportService   : Lookup MQTT transport adaptor JsonMqttAdaptor
2017-08-25 11:19:01.197  INFO 5565 --- [           main] o.i.i.server.mqtt.MqttTransportService   : Starting MQTT transport server
2017-08-25 11:19:01.210  INFO 5565 --- [           main] o.i.i.server.mqtt.MqttTransportService   : Mqtt transport started: 0.0.0.0:1883!
2017-08-25 11:19:01.224  INFO 5565 --- [           main] o.i.i.server.coap.CoapTransportService   : Starting CoAP transport...
2017-08-25 11:19:01.225  INFO 5565 --- [           main] o.i.i.server.coap.CoapTransportService   : Lookup CoAP transport adaptor JsonCoapAdaptor
2017-08-25 11:19:01.225  INFO 5565 --- [           main] o.i.i.server.coap.CoapTransportService   : Starting CoAP transport server
2017-08-25 11:19:01.228  INFO 5565 --- [           main] o.e.c.core.network.config.NetworkConfig  : Storing standard properties in file Californium.properties
2017-08-25 11:19:01.260  INFO 5565 --- [           main] org.eclipse.californium.core.CoapServer  : Starting server
2017-08-25 11:19:01.260  INFO 5565 --- [           main] o.e.c.core.network.CoapEndpoint          : Starting endpoint at /0.0.0.0:5683
2017-08-25 11:19:01.269  INFO 5565 --- [           main] o.i.i.server.coap.CoapTransportService   : CoAP transport started!
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: zookeeper.version=3.4.6-1569965, built on 02/20/2014 09:09 GMT
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: host.name=localhost
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.version=1.8.0_101
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.vendor=Oracle Corporation
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.class.path=iothub-1.0.0-boot.jar
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.library.path=/Users/xiningwang/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.io.tmpdir=/var/folders/zp/kmj0tf897hndh27m457zkzv40000gn/T/
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: java.compiler=<NA>
2017-08-25 11:19:01.273  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: os.name=Mac OS X
2017-08-25 11:19:01.274  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: os.arch=x86_64
2017-08-25 11:19:01.274  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: os.version=10.12.5
2017-08-25 11:19:01.274  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: user.name=xiningwang
2017-08-25 11:19:01.274  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: user.home=/Users/xiningwang
2017-08-25 11:19:01.274  INFO 5565 --- [           main] o.i.i.s.s.e.EnvironmentLogService        : environment: user.dir=/Users/xiningwang/localgit/iotplatform/iothub/target
nosqldaoconfig
2017-08-25 11:19:01.581  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerAdapter : Looking for @ControllerAdvice: org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@1936f0f5: startup date [Fri Aug 25 11:18:57 CST 2017]; root of context hierarchy
2017-08-25 11:19:01.644  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/api/v1/{deviceToken}/attributes],methods=[GET],produces=[application/json]}" onto public org.springframework.web.context.request.async.DeferredResult<org.springframework.http.ResponseEntity> org.iotp.iothub.server.http.DeviceApiController.getDeviceAttributes(java.lang.String,java.lang.String,java.lang.String,java.lang.String)
2017-08-25 11:19:01.645  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/api/v1/device/token/{deviceToken}/attributes/shadow],methods=[GET],produces=[application/json]}" onto public org.springframework.web.context.request.async.DeferredResult<org.springframework.http.ResponseEntity> org.iotp.iothub.server.http.DeviceApiController.getDeviceAttributesShadow(java.lang.String)
2017-08-25 11:19:01.645  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/api/v1/device/token/{deviceToken}/telemetry/shadow],methods=[GET],produces=[application/json]}" onto public org.springframework.web.context.request.async.DeferredResult<org.springframework.http.ResponseEntity> org.iotp.iothub.server.http.DeviceApiController.getDeviceTelemetryShadow(java.lang.String)
2017-08-25 11:19:01.646  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/api/v1/{deviceToken}/attributes],methods=[POST]}" onto public org.springframework.web.context.request.async.DeferredResult<org.springframework.http.ResponseEntity> org.iotp.iothub.server.http.DeviceApiController.postDeviceAttributes(java.lang.String,java.lang.String)
2017-08-25 11:19:01.646  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/api/v1/{deviceToken}/telemetry],methods=[POST]}" onto public org.springframework.web.context.request.async.DeferredResult<org.springframework.http.ResponseEntity> org.iotp.iothub.server.http.DeviceApiController.postTelemetry(java.lang.String,java.lang.String)
2017-08-25 11:19:01.647  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error]}" onto public org.springframework.http.ResponseEntity<java.util.Map<java.lang.String, java.lang.Object>> org.springframework.boot.autoconfigure.web.BasicErrorController.error(javax.servlet.http.HttpServletRequest)
2017-08-25 11:19:01.648  INFO 5565 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error],produces=[text/html]}" onto public org.springframework.web.servlet.ModelAndView org.springframework.boot.autoconfigure.web.BasicErrorController.errorHtml(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
2017-08-25 11:19:01.676  INFO 5565 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/webjars/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2017-08-25 11:19:01.676  INFO 5565 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2017-08-25 11:19:01.711  INFO 5565 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**/favicon.ico] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2017-08-25 11:19:01.958  INFO 5565 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2017-08-25 11:19:02.014  INFO 5565 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8081 (http)
2017-08-25 11:19:02.018  INFO 5565 --- [           main] o.i.i.server.IoTHubServerApplication     : Started IoTHubServerApplication in 4.599 seconds (JVM running for 5.076)
```