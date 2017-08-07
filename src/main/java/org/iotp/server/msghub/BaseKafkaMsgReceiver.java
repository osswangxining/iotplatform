package org.iotp.server.msghub;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.iotp.server.actors.service.ActorService;
import org.iotp.server.actors.service.DeviceSessionCtx;
import org.iotp.server.actors.service.adaptor.MqttTransportAdaptor;
import org.iotp.server.service.security.device.DeviceAuthService;
import org.iotp.server.transport.SessionMsgProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseKafkaMsgReceiver {
  @Autowired(required = false)
  protected ApplicationContext appContext;
  @Autowired(required = false)
  protected SessionMsgProcessor processor;
  @Autowired
  protected ActorService actorService;
  @Autowired(required = false)
  protected DeviceAuthService authService;
  @Value("${mqtt.adaptor}")
  protected String adaptorName;
  protected MqttTransportAdaptor adaptor;
  protected DeviceSessionCtx deviceSessionCtx;

  protected final Properties properties = new Properties();
  protected KafkaConsumer<String, String> kafkaConsumer;

  protected List<String> topicNames = new ArrayList<>();

  @PostConstruct
  public void init() {
    this.adaptor = (MqttTransportAdaptor) appContext.getBean(adaptorName);
    this.deviceSessionCtx = new DeviceSessionCtx(processor, authService, adaptor);

    properties.put("metadata.broker.list", "127.0.0.1:9092");
    properties.put("bootstrap.servers", "127.0.0.1:9092");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("zookeeper.connect", "127.0.0.1:2181");
    properties.put("auto.offset.reset", "latest");
    properties.put("enable.auto.commit", "false");
    properties.put("max.poll.records", "500");
    properties.put("queue.buffering.max.ms", "100");
    properties.put("queue.enqueue.timeout.ms", "-1");
    properties.put("metadata.fetch.timeout.ms", "10000");
    properties.put("request.required.acks", "-1");
    properties.put("producer.type", "sync");
    properties.put("serializer.class", "kafka.serializer.StringEncoder");
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group001");
    properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
    properties.put("fetch.message.max.bytes", "40971520");
    properties.put("max.partition.fetch.bytes", "40971520");

    // Figure out where to start processing messages from
    kafkaConsumer = new KafkaConsumer<String, String>(properties);

    kafkaConsumer.subscribe(topicNames);
    log.info("Kafka Consumer is started....");

    ConsumerThread thread = new ConsumerThread();
    thread.start();
  }

  @PreDestroy
  public void destroy() {
    log.info("Stopping Kafka Consumer....");
    try {
      kafkaConsumer.close();
    } catch (Exception e) {
      log.error("Failed to close Kafka Consumer during destroy()", e);
      throw new RuntimeException(e);
    }
    log.info("Kafka Consumer is stopped now....");
  }

  protected abstract boolean consume(ConsumerRecords<String, String> records);

  class ConsumerThread extends Thread {
    public void run() {
      while (true) {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(60000);
        if (records != null && !records.isEmpty()) {
          log.info("records size:{}", records.count());

          boolean success = consume(records);
          if (success) {
            log.info("now commit offset");
            kafkaConsumer.commitSync();
          }
        }
      }
    }

  }
}
