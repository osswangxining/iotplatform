package org.iotp.server.msghub;

import java.util.Arrays;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.iotp.analytics.ruleengine.common.msg.session.BasicAdaptorToSessionActorMsg;
import org.iotp.analytics.ruleengine.common.msg.session.BasicToDeviceActorSessionMsg;
import org.iotp.analytics.ruleengine.common.msg.session.FromDeviceMsg;
import org.iotp.infomgt.data.Device;
import org.iotp.infomgt.data.id.DeviceId;
import org.iotp.server.transport.JsonConverter;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaMsgReceiver4MQTT extends BaseKafkaMsgReceiver {

  public KafkaMsgReceiver4MQTT() {
    super();
    topicNames = Arrays.asList(ThingsMetaKafkaTopics.TELEMETRY_TOPIC);
  }
  
  public boolean consume(ConsumerRecords<String, String> records) {
    for (ConsumerRecord<String, String> consumerRecord : records) {
      log.info("topic:{}, key:{}, value:{}", consumerRecord.topic(), consumerRecord.key(), consumerRecord.value());
      String deviceId = consumerRecord.key();
      String payload = consumerRecord.value();
      Optional<Device> deviceOpt = authService.findDeviceById(DeviceId.fromString(deviceId));

      if (deviceOpt.isPresent()) {
        Device device = deviceOpt.get();
        JsonObject root = (JsonObject) new JsonParser().parse(payload);
        int messageId = root.getAsJsonPrimitive("messageId").getAsInt();
        FromDeviceMsg msg = JsonConverter.convertToTelemetry(root.get("d"), messageId);
        BasicToDeviceActorSessionMsg basicToDeviceActorSessionMsg = new BasicToDeviceActorSessionMsg(device,
            new BasicAdaptorToSessionActorMsg(deviceSessionCtx, msg));
        processor.process(basicToDeviceActorSessionMsg);
      }

    }
    return true;
  }
}
