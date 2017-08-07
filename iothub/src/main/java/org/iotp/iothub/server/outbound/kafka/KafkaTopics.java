package org.iotp.iothub.server.outbound.kafka;

/**
 */
public class KafkaTopics {

    public static final String DEVICE_RPC_RESPONSE_TOPIC = "rpc-response";
    public static final String DEVICE_RPC_REQUESTS_TOPIC = "rpc-request";
    public static final String DEVICE_ATTRIBUTES_RESPONSE_TOPIC_PREFIX = "attributes-response";
    public static final String DEVICE_ATTRIBUTES_REQUEST_TOPIC_PREFIX = "attributes-request";
    public static final String DEVICE_TELEMETRY_TOPIC = "telemetry";
    public static final String DEVICE_ATTRIBUTES_TOPIC = "attributes";

    public static final String BASE_GATEWAY_TOPIC = "gateway";
    public static final String GATEWAY_ATTRIBUTES_TOPIC = BASE_GATEWAY_TOPIC + "-attributes";
    public static final String GATEWAY_TELEMETRY_TOPIC = BASE_GATEWAY_TOPIC + "-telemetry";
    public static final String GATEWAY_RPC_TOPIC = BASE_GATEWAY_TOPIC + "-rpc";
    public static final String GATEWAY_ATTRIBUTES_REQUEST_TOPIC = GATEWAY_ATTRIBUTES_TOPIC + "-request";
    public static final String GATEWAY_ATTRIBUTES_RESPONSE_TOPIC = GATEWAY_ATTRIBUTES_TOPIC + "-response";

    private KafkaTopics() {
    }
}
