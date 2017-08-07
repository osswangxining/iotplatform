package org.iotp.server.msghub;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaMsgProducer {

  private Producer<String, String> producer;
  private final Properties properties = new Properties();

  @PostConstruct
  public void init() {
    properties.put("bootstrap.servers", "127.0.0.1:9092");
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    properties.put("acks", "-1");
    properties.put("retries", 0);
    properties.put("batch.size", 16384);
    properties.put("linger.ms", 0);
    properties.put("buffer.memory", 33554432);
    try {
      this.producer = new KafkaProducer<>(properties);
    } catch (Exception e) {
      log.error("Failed to start kafka producer", e);
      throw new RuntimeException(e);
    }
    log.info("Kafka Producer is started....");
  }

  public void send(String topic, String key, String value) {
    this.producer.send(new ProducerRecord<>(topic, key, value), (metadata, e) -> {
      if (e == null) {
        log.info("topic:{}, partition:{}, offset:{}", metadata.topic(), metadata.partition(), metadata.offset());
      } else {
        log.error("topic:{}, partition:{}, offset:{}, exception: {}", metadata.topic(), metadata.partition(),
            metadata.offset(), e.getMessage());
      }
    });
  }

  @PreDestroy
  public void destroy() {
    log.info("Stopping Kafka Producer....");
    try {
      this.producer.close();
    } catch (Exception e) {
      log.error("Failed to close producer during destroy()", e);
      throw new RuntimeException(e);
    }
    log.info("Kafka Producer is stopped now....");
  }
}
